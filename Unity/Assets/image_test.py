import cv2
import dlib
import os
from imutils import face_utils
import socket
import numpy as np
import time
from fer import FER
from _thread import *

host = "192.168.0.4"
port = 4321
detector = dlib.get_frontal_face_detector()
predictor = dlib.shape_predictor("shape_predictor_68_face_landmarks.dat")
emotion_model = FER(mtcnn=True)
back_number = 6
item_number = 3
capture_imgpath = os.path.join(os.getcwd(), "capture.jpg")

def main():
    server_sock = socket.socket(socket.AF_INET)
    server_sock.bind((host, port))
    server_sock.listen(5)
    print("<<<<<<<<<<<<Server is open>>>>>>>>>>>>>")
    while True:
        print("Waiting ........... ")
        client_sock, addr = server_sock.accept()
        start_new_thread(socket_thread, (client_sock, addr))
    server_sock.close()

def pixel_distance(pix1, pix2):
    return ((pix1[0]-pix2[0])**2+(pix1[1]-pix2[1])**2)**(1/2)

def get_bytes_stream(sock, length):
    buf = b''
    try:
        step = length
        while True:
            data = sock.recv(step)
            buf += data
            if len(buf) == length:
                break
            elif len(buf) < length:
                step = length - len(buf)
    except Exception as e:
        print(e)
    return buf[:length]

def socket_thread(client_sock, addr):
    print("Connected by :", addr[0], ':', addr[1])
    while True:
        try:
            len_bytes_string = bytearray(client_sock.recv(1024))[2:]
            if not len_bytes_string:
                print("Disconnected by",addr[0],":",addr[1])
                break
            len_bytes = len_bytes_string.decode("utf-8")
            length = int(len_bytes)

            if length > back_number:
                img = get_bytes_stream(client_sock, length)
                img_arr = np.frombuffer(img, dtype=np.uint8)
                image = cv2.imdecode(img_arr, cv2.IMREAD_COLOR)

                s = socket.socket()
                s.connect((host, 11111))

                gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
                faces = detector(gray)
                results = emotion_model.detect_emotions(image)

                for result in results:
                    bounding_box, emotions = result['box'], result['emotions']
                    print(emotions)
                    emotion = sorted(emotions.items(), key=lambda x: x[1], reverse=True)[0][0]
                    emotion_bytes = bytes(emotion.encode())
                    emotion_length = len(emotion_bytes)
                    client_sock.sendall(emotion_length.to_bytes(4, byteorder="big"))
                    client_sock.sendall(emotion_bytes)


                for face in faces:
                    shape = predictor(gray, face)
                    shape = face_utils.shape_to_np(shape)
                    x1 = face.left()
                    y1 = face.top()
                    x2 = face.right()
                    y2 = face.bottom()

                    box_width = int(y2 - y1)
                    box_height = int(x2 - x1)

                    lower_point = (shape[62] + shape[66]) / 2
                    upper_point = (shape[48] + shape[54]) / 2
                    mouth_smile = 1250 * (lower_point[1] - upper_point[1]) / box_height
                    mouth_open = 350 * pixel_distance(shape[62], shape[66]) / box_width

                    right_eye_distance = 100 * (
                            1 - 2 * pixel_distance((shape[43] + shape[44]) / 2,
                                                   (shape[47] + shape[46]) / 2) / pixel_distance(
                        shape[42], shape[45]))
                    left_eye_distance = 100 * (
                            1 - 2 * pixel_distance((shape[37] + shape[38]) / 2,
                                                   (shape[41] + shape[40]) / 2) / pixel_distance(
                        shape[36], shape[39]))
                    eye_threshold = 60
                    if right_eye_distance >= eye_threshold:
                        right_eye_distance = 100
                    if left_eye_distance >= eye_threshold:
                        left_eye_distance = 100

                    s.send((
                                   str(mouth_smile) + "," +
                                   str(mouth_open) + "," +
                                   str(right_eye_distance) + "," +
                                   str(left_eye_distance)).encode()
                           )
                    s.close()

            elif length == back_number:
                backs = client_sock.recv(length)
                s = socket.socket()
                s.connect((host, 33333))
                s.send(backs)
                s.close()

            else:
                items = client_sock.recv(length)
                s = socket.socket()
                s.connect((host, 55555))
                s.send(items)
                s.close()

            time.sleep(0.5)
            with open(capture_imgpath, "rb") as f:
                data = f.read()
                data_length = len(data)
                client_sock.sendall(data_length.to_bytes(4, byteorder="big"))
                client_sock.sendall(data)

        except ConnectionError as e:
            print("Disconnected by",addr[0],":",addr[1])
            break

    client_sock.close()


if __name__ == '__main__':
    main()