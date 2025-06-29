import cv2
import requests
import time
from datetime import datetime

# ==== CONFIG ====
BACKEND_URL = "http://localhost:8080/api/upload"
VEHICLE_ID = "VH1234"
CAMERA_ID = 0  # Default webcam (use 1 or 2 for external cameras)


def send_frame(frame, vehicle_id):
    # Encode frame as JPEG
    success, buffer = cv2.imencode('.jpg', frame)
    if not success:
        print("Failed to encode image")
        return

    img_bytes = buffer.tobytes()
    timestamp = datetime.utcnow().isoformat()

    # Prepare multipart/form-data
    files = {
        'image': ('frame.jpg', img_bytes, 'image/jpeg')
    }
    data = {
        'vehicleId': vehicle_id,
        'timestamp': timestamp
    }

    try:
        response = requests.post(BACKEND_URL, files=files, data=data)
        print(f"[{timestamp}] Response: {response.status_code} - {response.text}")
    except Exception as e:
        print(f"Error sending frame: {e}")


def main():
    cap = cv2.VideoCapture(CAMERA_ID)

    if not cap.isOpened():
        print("Error: Camera could not be opened.")
        return

    print("Starting camera feed. Press 'q' to quit...")

    while True:
        ret, frame = cap.read()
        if not ret:
            print("Failed to grab frame")
            break

        send_frame(frame, VEHICLE_ID)

        cv2.imshow("Camera", frame)

        #  1 frame per second (adjustable)
        if cv2.waitKey(1000) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()


if __name__ == "__main__":
    main()
