from flask import Flask, request, jsonify
import tensorflow as tf
from flask_cors import CORS
import numpy as np
import joblib
import os

app = Flask(__name__)
CORS(app)

# Load model dan scaler
try:
    model = tf.keras.models.load_model("tukang_recommender.h5", custom_objects={'mse': tf.keras.losses.MeanSquaredError()})
    print("Model berhasil dimuat!")
except Exception as e:
    print(f"Terjadi kesalahan saat memuat model: {e}")

try:
    scaler = joblib.load("scaler.pkl")
    print("Scaler berhasil dimuat!")
except Exception as e:
    print(f"Terjadi kesalahan saat memuat scaler: {e}")

# Route untuk health check
@app.route('/', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'berjalan',
        'pesan': 'API Tukang Recommender sedang berjalan'
    })

# Route untuk prediksi
@app.route('/predict', methods=['GET'])
def predict():
    try:
        # Mengambil parameter dari URL
        distance = request.args.get('distance', default=10.0, type=float)  # Default 10.0 km
        rate = request.args.get('rate', default=5.0, type=float)  # Default rating 5.0

        # Menyiapkan data input
        input_data = np.array([[distance, rate]])

        # Melakukan scaling
        scaled_input = scaler.transform(input_data)

        # Melakukan prediksi
        prediction = model.predict(scaled_input)[0][0]

        return jsonify({
            'preference_score': float(prediction)
        })
    except Exception as e:
        return jsonify({
            'error': f"Terjadi kesalahan: {e}"
        }), 500

# Handler untuk error umum
@app.errorhandler(Exception)
def handle_exception(e):
    return jsonify({
        'error': str(e),
        'status': 'error'
    }), 500

# Handler untuk 404 Not Found
@app.errorhandler(404)
def not_found_error(error):
    return jsonify({
        'error': 'Endpoint tidak ditemukan',
        'status': 'error'
    }), 404

# Handler untuk 405 Method Not Allowed
@app.errorhandler(405)
def method_not_allowed_error(error):
    return jsonify({
        'error': 'Metode HTTP tidak diizinkan',
        'status': 'error'
    }), 405

if __name__ == '__main__':
    print("Memulai aplikasi...")
    try:
        # Print model summary untuk debugging
        model.summary()
        print("Model berhasil dimuat!")
    except Exception as e:
        print(f"Error saat memuat model: {str(e)}")

    # Menggunakan port dari environment variable untuk Heroku
    port = int(os.environ.get("PORT", 5000))

    # Jalankan aplikasi
    app.run(host='0.0.0.0', port=port)