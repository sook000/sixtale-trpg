import { defineStore } from "pinia";
import { ref } from "vue";
import Peer from 'peerjs';

export const useSessionStore = defineStore("session", () => {
  const peer = ref(null); // PeerJS 인스턴스
  const connections = ref(Array(9).fill(null)); // 사용자별 연결 저장
  const streams = ref(Array(9).fill(null)); // 사용자별 스트림 저장
  const voiceStates = ref(Array(9).fill(false)); // 사용자별 음성 상태 저장

  // 세션을 초기화하는 함수
  const initializeSession = (userId) => {
    if (!peer.value) {
      peer.value = new Peer(userId, {
        host: '0.peerjs.com', // PeerJS 공개 서버 URL
        port: 443, // HTTPS를 사용하는 포트
        secure: true, // HTTPS 연결 사용
      });

      peer.value.on('open', (id) => {
        console.log(`PeerJS connected with ID: ${id}`);
      });

      peer.value.on('call', (call) => {
        navigator.mediaDevices.getUserMedia({ audio: true, video: false }).then((stream) => {
          call.answer(stream); // 상대방의 통화 요청에 스트림을 제공
          call.on('stream', (remoteStream) => {
            const subUserId = parseInt(call.peer, 10);
            console.log(`Receiving stream from userId: ${subUserId}`);
            streams.value[subUserId - 1] = remoteStream;

            // 오디오 요소에 연결하여 재생 (로컬 재생 제거)
            const audioElement = new Audio();
            audioElement.srcObject = remoteStream;
            audioElement.play();
          });
        }).catch((error) => {
          console.error("Error accessing user media:", error);
        });
      });
    }
  };

  // 음성 채팅을 시작하는 함수
  const startVoiceChat = async (userId) => {
    if (!peer.value) {
      initializeSession(userId);
    }
    
    navigator.mediaDevices.getUserMedia({ audio: true, video: false })
      .then((stream) => {
        streams.value[userId - 1] = stream;
        voiceStates.value[userId - 1] = true;

        console.log(`Microphone is now active for user ${userId}`);

        // 모든 연결된 피어에 대해 통화 시작
        connections.value.forEach((conn, index) => {
          if (index !== userId - 1 && conn) {
            const call = peer.value.call(conn.peer, stream);
            call.on('stream', (remoteStream) => {
              console.log(`Receiving stream from userId: ${index + 1}`);
              streams.value[index] = remoteStream;

              // 원격 스트림을 재생
              const audioElement = new Audio();
              audioElement.srcObject = remoteStream;
              audioElement.play();
            });
          }
        });
      })
      .catch((err) => {
        console.error("Error accessing microphone:", err);
      });
  };

  // 음성 채팅을 중지하는 함수
  const stopVoiceChat = (userId) => {
    const stream = streams.value[userId - 1];
    
    if (stream) {
      stream.getTracks().forEach(track => track.stop()); // 스트림 정지
      streams.value[userId - 1] = null; // 스트림 초기화
      voiceStates.value[userId - 1] = false; // 음성 상태 초기화

      console.log(`Microphone is now inactive for user ${userId}`);

      // 연결된 모든 피어의 통화 종료
      connections.value.forEach((conn, index) => {
        if (index !== userId - 1 && conn) {
          conn.close();
          connections.value[index] = null;
        }
      });
    }
  };

  // 음성 채팅을 토글하는 함수
  const toggleVoiceChat = (userId) => {
    if (voiceStates.value[userId - 1]) {
      stopVoiceChat(userId);
    } else {
      startVoiceChat(userId);
    }
  };

  // 세션 연결을 해제하는 함수
  const disconnect = () => {
    if (peer.value) {
      peer.value.destroy();
      console.log("PeerJS session disconnected");
      peer.value = null; // PeerJS 인스턴스 초기화

      // 모든 사용자 음성 상태 초기화
      voiceStates.value.fill(false);
      streams.value.fill(null);
      connections.value.fill(null);
    }
  };

  // 음성 상태 확인 함수
  const isVoiceOn = (userId) => {
    return voiceStates.value[userId - 1];
  };

  return {
    initializeSession,
    toggleVoiceChat,
    disconnect,
    isVoiceOn,
  };
});


// import { defineStore } from "pinia";
// import { ref } from "vue";
// import Peer from 'peerjs';

// export const useSessionStore = defineStore("session", () => {
//   const peer = ref(null); // PeerJS 인스턴스
//   const connections = ref(Array(9).fill(null)); // 사용자별 연결 저장
//   const streams = ref(Array(9).fill(null)); // 사용자별 스트림 저장
//   const voiceStates = ref(Array(9).fill(false)); // 사용자별 음성 상태 저장

//   // 세션을 초기화하는 함수
//   const initializeSession = (userId) => {
//     if (!peer.value) {
//       peer.value = new Peer(userId, {
//         host: 'your-domain.com', // 자체 서버의 도메인 또는 IP 주소
//         port: 9000, // 서버에서 설정한 포트
//         path: '/', // 필요한 경우 경로 설정
//         secure: true, // HTTPS 사용 여부
//       });

//       peer.value.on('open', (id) => {
//         console.log(`PeerJS connected with ID: ${id}`);
//       });

//       peer.value.on('call', (call) => {
//         navigator.mediaDevices.getUserMedia({ audio: true, video: false }).then((stream) => {
//           call.answer(stream); // 상대방의 통화 요청에 스트림을 제공
//           call.on('stream', (remoteStream) => {
//             const subUserId = parseInt(call.peer, 10);
//             console.log(`Receiving stream from userId: ${subUserId}`);
//             streams.value[subUserId - 1] = remoteStream;

//             // 오디오 요소에 연결하여 재생 (로컬 재생 제거)
//             const audioElement = new Audio();
//             audioElement.srcObject = remoteStream;
//             audioElement.play();
//           });
//         }).catch((error) => {
//           console.error("Error accessing user media:", error);
//         });
//       });
//     }
//   };

//   // 음성 채팅을 시작하는 함수
//   const startVoiceChat = async (userId) => {
//     if (!peer.value) {
//       initializeSession(userId);
//     }
    
//     navigator.mediaDevices.getUserMedia({ audio: true, video: false })
//       .then((stream) => {
//         streams.value[userId - 1] = stream;
//         voiceStates.value[userId - 1] = true;

//         console.log(`Microphone is now active for user ${userId}`);

//         // 모든 연결된 피어에 대해 통화 시작
//         connections.value.forEach((conn, index) => {
//           if (index !== userId - 1 && conn) {
//             const call = peer.value.call(conn.peer, stream);
//             call.on('stream', (remoteStream) => {
//               console.log(`Receiving stream from userId: ${index + 1}`);
//               streams.value[index] = remoteStream;

//               // 원격 스트림을 재생
//               const audioElement = new Audio();
//               audioElement.srcObject = remoteStream;
//               audioElement.play();
//             });
//           }
//         });
//       })
//       .catch((err) => {
//         console.error("Error accessing microphone:", err);
//       });
//   };

//   // 음성 채팅을 중지하는 함수
//   const stopVoiceChat = (userId) => {
//     const stream = streams.value[userId - 1];
    
//     if (stream) {
//       stream.getTracks().forEach(track => track.stop()); // 스트림 정지
//       streams.value[userId - 1] = null; // 스트림 초기화
//       voiceStates.value[userId - 1] = false; // 음성 상태 초기화

//       console.log(`Microphone is now inactive for user ${userId}`);

//       // 연결된 모든 피어의 통화 종료
//       connections.value.forEach((conn, index) => {
//         if (index !== userId - 1 && conn) {
//           conn.close();
//           connections.value[index] = null;
//         }
//       });
//     }
//   };

//   // 음성 채팅을 토글하는 함수
//   const toggleVoiceChat = (userId) => {
//     if (voiceStates.value[userId - 1]) {
//       stopVoiceChat(userId);
//     } else {
//       startVoiceChat(userId);
//     }
//   };

//   // 세션 연결을 해제하는 함수
//   const disconnect = () => {
//     if (peer.value) {
//       peer.value.destroy();
//       console.log("PeerJS session disconnected");
//       peer.value = null; // PeerJS 인스턴스 초기화

//       // 모든 사용자 음성 상태 초기화
//       voiceStates.value.fill(false);
//       streams.value.fill(null);
//       connections.value.fill(null);
//     }
//   };

//   // 음성 상태 확인 함수
//   const isVoiceOn = (userId) => {
//     return voiceStates.value[userId - 1];
//   };

//   return {
//     initializeSession,
//     toggleVoiceChat,
//     disconnect,
//     isVoiceOn,
//   };
// });
