# FaceTransferApplication
---
- 연세대학교 전기전자공학부 학부 수업인 응용프로그래밍에서 진행한 final project source code입니다.

- 구성은 Unity, Application으로 되어있는데 python server 코드는 여기를 참고해주시면 됩니다.

> Unity
> > Asset
> > > image_test.py
> > > ...

- 또한 Unity project는 그대로 Unity폴더를 실행하면 됩니다. 에셋들을 그대로 넣어놨습니다.

- Python 환경은 다음과 같아요.

```
  pip install pytorch
  pip install tensorflow
  pip install cmake
  pip install dlib
  pip install FER(fer) 둘 중 하나
  pip install opencv-python
  pip install imutils
  pip install socket
```

- 위의 환경을 설치하는데, dlib가 설치가 안되시는 분은 다음 링크를 봐주시면 됩니다.
  https://updaun.tistory.com/entry/python-python-37-dlib-install-error
  
- 전체적인 구조는 python to unity가 서버로 구성되어있으며, python to java는 클라이언트와 서버쪽 통신입니다.

- gethost로 기본적인 셋팅은 실행하면 알아서 되겠지만, 고정 ip가 서버로 쓰기에는 편리하고 좀 더 안정적이에요.


# 개발 일지
---
1. Blender 공부

2. 팀원들과 아이디어 공유 및 구현 가능한 형태를 제시

3. 추가 기능 논의 및 자료조사 실시

4. 첫 발표 후 방향을 제대로 잡고 개발 시작

5. 익숙한 언어인 python 개발 시작. 오픈 소스를 활용

6. C# 프로그래밍을 통해 간단히 로컬에서 python to unity 구현

7. java 코딩을 통해 UI를 구성하고, 파이썬과 연결

8. 추가 작업 및 마무리
