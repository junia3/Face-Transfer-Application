import socket
def get_bytes_stream(sock, length):
    buf = b''
    try:
        step = length
        # print("length: ", length)
        while True:
            # print("step:", step)
            data = sock.recv(step)
            buf += data
            if len(buf) == length:
                break
            elif len(buf) < length:
                step = length - len(buf)
    except Exception as e:
        print(e)
    return buf[:length]


host = '192.168.0.4'
port = 1234

server_sock = socket.socket(socket.AF_INET)
server_sock.bind((host, port))
server_sock.listen(2)
result = ''
while True:
    print("기다리는 중")
    client_sock, addr = server_sock.accept()

    len_bytes_string = bytearray(client_sock.recv(1024))[2:]
    len_bytes = len_bytes_string.decode("utf-8")
    length = int(len_bytes)

    img = get_bytes_stream(client_sock, length)
    with open("img_from_client.jpg", "wb") as f:
        f.write(img)

client_sock.close()
server_sock.close()