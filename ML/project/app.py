from flask import Flask, request, jsonify
import tensorflow as tf
import numpy as np
from tensorflow.keras.losses import MeanSquaredError

# Load the trained model
model = tf.keras.models.load_model('model.h5', custom_objects={'mse': MeanSquaredError()})

# Initialize the Flask app
app = Flask(__name__)

@app.route('/')
def home():
    return "API is working. Use the endpoint '/predict' with a POST method to make predictions."

@app.route('/predict', methods=['GET', 'POST'])
def predict():
    if request.method == 'GET':
        # Provide a message when the endpoint is accessed via GET
        return """
            This endpoint requires a POST method with JSON payload to make predictions.
            Example payload (JSON format):
            {
                "Kebutuhan": 1,
                "Umur_Pengguna": 30,
                "JenisKelamin_Pengguna": 0,
                "Spesialis": 2,
                "JenisKelamin_Tukang": 1
            }
            """

    # Handle POST request
    data = request.json
    features = np.array([[
        data['Kebutuhan'],
        data['Umur_Pengguna'],
        data['JenisKelamin_Pengguna'],
        data['Spesialis'],
        data['JenisKelamin_Tukang']
    ]])
    prediction = model.predict(features)
    return jsonify({'predicted_rating': float(prediction[0][0])})

if __name__ == '__main__':
    app.run(debug=True)
