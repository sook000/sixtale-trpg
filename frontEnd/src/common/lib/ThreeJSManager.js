import * as THREE from 'three'; // Three.js를 가져옴
import TWEEN from '@tweenjs/tween.js'; // Tween.js를 가져옴
import * as CANNON from 'cannon'; // Cannon.js를 가져옴
import eventBus from '@/common/lib/eventBus.js'; // 이벤트 버스를 가져옴
import { RoundedBoxGeometry } from 'three/examples/jsm/geometries/RoundedBoxGeometry.js';
import { BoxLineGeometry } from 'three/examples/jsm/geometries/BoxLineGeometry.js';

class ThreeJSManager {
  constructor(container) {
    this.container = container; // 컨테이너 엘리먼트를 저장
    this.scene = new THREE.Scene(); // Three.js 장면을 생성
    this.camera = new THREE.PerspectiveCamera(75, container.clientWidth / container.clientHeight, 0.1, 1000); // 카메라 설정
    this.updateCameraAndWalls(); // 초기 설정 시 카메라와 벽 업데이트
    this.renderer = new THREE.WebGLRenderer({ alpha: true }); // 렌더러 생성
    this.renderer.setSize(container.clientWidth, container.clientHeight); // 렌더러 크기 설정

    container.appendChild(this.renderer.domElement); // 렌더러 DOM 엘리먼트를 컨테이너에 추가
    this.clock = new THREE.Clock(); // 시계 생성
    const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
    this.scene.add(ambientLight);
    const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
    directionalLight.position.set(5, 10, 7);
    this.scene.add(directionalLight);
    this.diceMeshes = []; // 주사위 메쉬 배열 초기화
    this.diceBodies = []; // 주사위 물리 바디 배열 초기화
    this.world = new CANNON.World(); // 물리 세계 생성
    this.world.gravity.set(0, -15, 0); // 중력 설정
    this.world.broadphase = new CANNON.NaiveBroadphase(); // 브로드페이즈 설정
    this.world.solver.iterations = 10; // 솔버 반복 설정
    this.world.solver.tolerance = 0.1; // 솔버 허용오차 설정
    this.diceMaterial = new CANNON.Material('diceMaterial'); // 주사위 재질 생성
    this.floorMaterial = new CANNON.Material('floorMaterial'); // 바닥 재질 생성
    this.contactMaterial = new CANNON.ContactMaterial(this.diceMaterial, this.floorMaterial, { // 접촉 재질 생성
      friction: 0.4,
      restitution: 0.3,
    });
    this.world.addContactMaterial(this.contactMaterial); // 접촉 재질을 물리 세계에 추가

    this.addFloor(); // 바닥 추가
    this.addWalls(); // 벽 추가
    this.animate = this.animate.bind(this); // animate 메서드를 this에 바인딩
    this.animate(); // 애니메이션 시작

    eventBus.on('roll-dice', this.rollDice.bind(this)); // roll-dice 이벤트 구독
    window.addEventListener('resize', this.onWindowResize.bind(this)); // 창 크기 조정 이벤트 핸들러 추가

    // 여기서 pointer-events를 none으로 설정
    this.renderer.domElement.style.pointerEvents = 'none';

    // renderer-container 클래스에 pointer-events: none; 추가
    const rendererContainer = this.renderer.domElement.parentElement;
    if (rendererContainer && rendererContainer.classList.contains('renderer-container')) {
      rendererContainer.style.pointerEvents = 'none';
      
      // renderer-container 내의 모든 캔버스 요소에 대해 pointer-events: none; 추가
      const canvases = rendererContainer.querySelectorAll('canvas');
      canvases.forEach(canvas => {
        canvas.style.pointerEvents = 'none';
      });
    }
  }

  updateCameraAndWalls() {
    const aspect = this.container.clientWidth / this.container.clientHeight; // 종횡비 계산
    const zoom = 1; // 줌 레벨 설정
    this.camera.position.set(0, 10, 0); // 카메라 위치 설정
    this.camera.lookAt(0, 0, 0); // 카메라가 원점을 바라보게 설정
    this.camera.zoom = zoom; // 카메라 줌 설정
    this.camera.updateProjectionMatrix(); // 카메라 투영 행렬 업데이트

    this.wallSize = { // 벽 크기 설정
      width: 20 * aspect,
      height: 30,
      depth: 20,
    };
  }

  addFloor() {
    const floorShape = new CANNON.Plane(); // 바닥 모양 생성
    const floorBody = new CANNON.Body({ mass: 0 }); // 바닥 물리 바디 생성
    floorBody.addShape(floorShape); // 바닥 모양을 바디에 추가
    floorBody.quaternion.setFromEuler(-Math.PI / 2, 0, 0); // 바닥 회전 설정
    floorBody.position.y = -5; // 바닥 위치 설정
    this.world.addBody(floorBody); // 바닥 바디를 물리 세계에 추가

    const floorGeometry = new THREE.PlaneGeometry(this.wallSize.width, this.wallSize.depth); // 바닥 지오메트리 생성
    const floorMaterial = new THREE.MeshBasicMaterial({ color: 0x108080, side: THREE.DoubleSide, opacity: 0, transparent: true }); // 바닥 재질 생성
    const floorMesh = new THREE.Mesh(floorGeometry, floorMaterial); // 바닥 메쉬 생성
    floorMesh.rotation.x = -Math.PI / 2; // 바닥 메쉬 회전 설정
    floorMesh.position.y = -5; // 바닥 메쉬 위치 설정
    this.scene.add(floorMesh); // 바닥 메쉬를 장면에 추가
  }

  addWalls() {
    const wallHeight = this.wallSize.height; // 벽 높이 설정
    const halfWidth = this.wallSize.width / 3; // 벽 너비의 절반 계산
    const halfDepth = this.wallSize.depth / 3.5; // 벽 깊이의 절반 계산

    const walls = [ // 벽 설정 배열
      { x: -halfWidth, y: wallHeight / 2, z: 0, rotation: { x: 0, y: Math.PI / 2, z: 0 } },
      { x: halfWidth, y: wallHeight / 2, z: 0, rotation: { x: 0, y: -Math.PI / 2, z: 0 } },
      { x: 0, y: wallHeight / 2, z: -halfDepth, rotation: { x: 0, y: 0, z: 0 } },
      { x: 0, y: wallHeight / 2, z: halfDepth, rotation: { x: 0, y: Math.PI, z: 0 } },
      { x: 0, y: wallHeight, z: 0, rotation: { x: Math.PI / 2, y: 0, z: 0 } },
      { x: 0, y: -1, z: 0, rotation: { x: -Math.PI / 2, y: 0, z: 0 } },
    ];

    walls.forEach(({ x, y, z, rotation }) => {
      const wallShape = new CANNON.Plane(); // 벽 모양 생성
      const wallBody = new CANNON.Body({ mass: 0 }); // 벽 물리 바디 생성
      wallBody.addShape(wallShape); // 벽 모양을 바디에 추가
      wallBody.position.set(x, y, z); // 벽 위치 설정
      wallBody.quaternion.setFromEuler(rotation.x, rotation.y, rotation.z); // 벽 회전 설정
      this.world.addBody(wallBody); // 벽 바디를 물리 세계에 추가
    });

    const wallGeometry = new THREE.PlaneGeometry(this.wallSize.width, wallHeight); // 벽 지오메트리 생성
    const wallMaterial = new THREE.MeshBasicMaterial({ color: 0xFF0000, side: THREE.DoubleSide, opacity: 0, transparent: true }); // 벽 재질 생성

    const leftWall = new THREE.Mesh(wallGeometry, wallMaterial); // 왼쪽 벽 메쉬 생성
    leftWall.rotation.y = Math.PI / 2; // 왼쪽 벽 회전 설정
    leftWall.position.set(-halfWidth, wallHeight / 2 + 1, 0); // 왼쪽 벽 위치 설정
    this.scene.add(leftWall); // 왼쪽 벽을 장면에 추가

    const rightWall = new THREE.Mesh(wallGeometry, wallMaterial); // 오른쪽 벽 메쉬 생성
    rightWall.rotation.y = -Math.PI / 2; // 오른쪽 벽 회전 설정
    rightWall.position.set(halfWidth, wallHeight / 2 + 1, 0); // 오른쪽 벽 위치 설정
    this.scene.add(rightWall); // 오른쪽 벽을 장면에 추가

    const frontWall = new THREE.Mesh(new THREE.PlaneGeometry(this.wallSize.depth, wallHeight), wallMaterial); // 앞쪽 벽 메쉬 생성
    frontWall.rotation.y = 0; // 앞쪽 벽 회전 설정
    frontWall.position.set(0, wallHeight / 2 + 1, -halfDepth); // 앞쪽 벽 위치 설정
    this.scene.add(frontWall); // 앞쪽 벽을 장면에 추가

    const backWall = new THREE.Mesh(new THREE.PlaneGeometry(this.wallSize.depth, wallHeight), wallMaterial); // 뒤쪽 벽 메쉬 생성
    backWall.rotation.y = Math.PI; // 뒤쪽 벽 회전 설정
    backWall.position.set(0, wallHeight / 2 - 4, halfDepth); // 뒤쪽 벽 위치 설정
    this.scene.add(backWall); // 뒤쪽 벽을 장면에 추가
  }

  animate() {
    requestAnimationFrame(this.animate);
    TWEEN.update();
  
    const maxSubSteps = 3;
    const fixedTimeStep = 1 / 60;
    const deltaTime = Math.min(0.1, this.clock.getDelta());
  
    try {
      this.world.step(fixedTimeStep, deltaTime, maxSubSteps);
  
      this.diceBodies.forEach((body, index) => {
        const diceMesh = this.diceMeshes[index];
        if (diceMesh && body) {
          diceMesh.position.copy(body.position);
          diceMesh.quaternion.copy(body.quaternion);
          if (diceMesh instanceof THREE.Group) {
            // D100 주사위의 경우
            diceMesh.children[0].position.copy(body.position);
            diceMesh.children[0].quaternion.copy(body.quaternion);
            if (index + 1 < this.diceBodies.length) {
              const nextBody = this.diceBodies[index + 1];
              diceMesh.children[1].position.copy(nextBody.position);
              diceMesh.children[1].quaternion.copy(nextBody.quaternion);
            }
          } else {
            diceMesh.position.copy(body.position);
            diceMesh.quaternion.copy(body.quaternion);
          }
        }
      });
  
      this.renderer.render(this.scene, this.camera);
    } catch (error) {
      console.error("Error in animation loop:", error);
      cancelAnimationFrame(this.animationFrameId);
    }
  }
  clearDice() {
    this.diceMeshes.forEach(dice => {
      this.scene.remove(dice); // 주사위 메쉬를 장면에서 제거
      if (dice.geometry) dice.geometry.dispose(); // 주사위 지오메트리 삭제
      if (Array.isArray(dice.material)) {
        dice.material.forEach(mat => mat.dispose()); // 주사위 재질 배열 삭제
      } else if (dice.material) {
        dice.material.dispose(); // 주사위 재질 삭제
      }
    });
    this.diceMeshes = []; // 주사위 메쉬 배열 초기화
    this.diceBodies.forEach(body => {
      this.world.removeBody(body); // 주사위 바디를 물리 세계에서 제거
    });
    this.diceBodies = []; // 주사위 바디 배열 초기화
  }

  onWindowResize() {
    this.updateCameraAndWalls(); // 카메라와 벽을 업데이트
    this.renderer.setSize(this.container.clientWidth, this.container.clientHeight); // 렌더러 크기 조정
  }

 // 1. 주사위 타입 받아오기
 async rollDice(types) {
  this.clearDice(); // 기존 주사위 제거

  const results = [];
  for (let i = 0; i < types.length; i++) {
    const dice = types[i];
    if (dice.type === 100) {
      // D100의 경우 101과 102로 나누어 순차적으로 처리
      const result1 = await this.rollSingleDice(101);
      const result2 = await this.rollSingleDice(102);
      const d100Result = result1.value * 10 + result2.value;
      results.push({ type: 100, value: d100Result });
    } else {
      const result = await this.rollSingleDice(dice.type);
      results.push(result);
    }

    // 마지막 주사위가 아니라면 2초 대기 (주사위가 사라지는 시간)
    if (i < types.length - 1) {
      await this.waitForStabilization(2000);
    }
  }

  return results;
}

// 주사위 안정화를 기다리는 보조 함수
waitForStabilization(ms) {
  return new Promise(resolve => setTimeout(resolve, ms));
}

// 2. 주사위 굴리기
rollSingleDice(type) {
  
  return new Promise((resolve) => {
    let diceMesh, body;
    
    const dice = this.createDice(type);
    diceMesh = dice.diceMesh;
    body = dice.body;
  
    // // 벽 안에서 생성
    // const startX = (Math.random() * (this.wallSize.width - 4) - (this.wallSize.width / 2 - 2));
    // const startY = 1 + Math.random() * 1;
    // const startZ = (Math.random() * (this.wallSize.depth - 4) - (this.wallSize.depth / 2 - 2));
  
    // 우하단에서 시작하도록 위치 설정
    const startX = 7
    const startY = 0 + Math.random() * 1; // 약간 위에서 시작
    const startZ = 4
    // (Math.random() * (this.wallSize.depth - 4) - (this.wallSize.depth / 2 - 2));


    diceMesh.position.set(startX, startY, startZ);
    body.position.set(startX, startY, startZ);
    this.scene.add(diceMesh);
    this.diceMeshes.push(diceMesh);
    this.diceBodies.push(body);
    this.world.addBody(body);
    // console.log('Added dice to scene:', diceMesh);
    // 주사위에 힘을 가함
    const maxForce = 100;
    const force = new CANNON.Vec3(
      -20 + Math.random() * 5,
      0 + Math.random() * 1.5,
      -20 + Math.random() * 10

      // -(Math.random() - 0.5) * maxForce,
      // (Math.random() - 0.5) * maxForce / 90,
      // (Math.random() - 0.5) * maxForce
    );
    body.applyImpulse(force, new CANNON.Vec3(0, 0, 0));
  
    // 주사위에 회전 속도를 설정
    const angularVelocity = new CANNON.Vec3(
      Math.random() * 5 - 2.5,
      Math.random() * 5 - 2.5,
      Math.random() * 5 - 2.5
    );
    body.angularVelocity.set(angularVelocity.x, angularVelocity.y, angularVelocity.z);
    let stableCount = 0;
    let isStable = false;
    let resultReported = false;
    let lastPosition = new CANNON.Vec3();
    let lastQuaternion = new CANNON.Quaternion();

    const checkStability = setInterval(() => {
      const velocityThreshold = 0.1;
      const angularVelocityThreshold = 0.1;
      const positionThreshold = 0.01;
      const quaternionThreshold = 0.01;

      const isVelocityLow = body.velocity.lengthSquared() < velocityThreshold;
      const isAngularVelocityLow = body.angularVelocity.lengthSquared() < angularVelocityThreshold;
      const isPositionStable = body.position.distanceTo(lastPosition) < positionThreshold;
      
      const quatDiff = new CANNON.Quaternion();
      quatDiff.mult(body.quaternion, lastQuaternion.inverse());
      const angle = 2 * Math.acos(Math.abs(quatDiff.w));
      const isQuaternionStable = angle < quaternionThreshold;

      if (isVelocityLow && isAngularVelocityLow && isPositionStable && isQuaternionStable) {
        stableCount++;
        if (stableCount > 15 && !isStable) { // 0.5초 동안 안정적이면 멈춘 것으로 간주
          isStable = true;
          if (!resultReported) {
            resultReported = true;
            const result = this.getTopFaceValue(diceMesh);
            console.log(`Dice result: D${type} = ${result}`);
            resolve({ type, value: result });

            // 2초 후 주사위 제거
            setTimeout(() => {
              this.clearSingleDice(diceMesh, body);
              clearInterval(checkStability);
            }, 2000);
          }
        }
      } else {
        stableCount = 0;
      }

      lastPosition.copy(body.position);
      lastQuaternion.copy(body.quaternion);
    }, 16);

    // 안전장치: 10초 후에도 안정화되지 않으면 강제로 결과 반환
    setTimeout(() => {
      if (!isStable) {
        clearInterval(checkStability);
        if (!resultReported) {
          resultReported = true;
          const result = this.getTopFaceValue(diceMesh);
          console.log(`Forced dice result: D${type} = ${result}`);
          resolve({ type, value: result });

          // 2초 후 주사위 제거
          setTimeout(() => {
            this.clearSingleDice(diceMesh, body);
          }, 2000);
        }
      }
    }, 10000);
  });
}
clearSingleDice(diceMesh, body) {
  this.scene.remove(diceMesh);
  if (diceMesh.geometry) diceMesh.geometry.dispose();
  if (Array.isArray(diceMesh.material)) {
    diceMesh.material.forEach(mat => mat.dispose());
  } else if (diceMesh.material) {
    diceMesh.material.dispose();
  }
  this.diceMeshes = this.diceMeshes.filter(mesh => mesh !== diceMesh);
  this.world.removeBody(body);
  this.diceBodies = this.diceBodies.filter(b => b !== body);
}
 // 3. 주사위 지오메트리 생성
  createDice(type) {
    let geometry;
    let diceMesh;
    const scale = 1;
    let faceValues;
    const octahedronVertices = [
      1, 0, 0,   -1, 0, 0,   0, 1, 0,
      0, -1, 0,  0, 0, 1,   0, 0, -1
    ];
  
    const octahedronIndices = [
      0, 2, 4,   0, 4, 3,   0, 3, 5,   0, 5, 2,
      1, 2, 5,   1, 5, 3,   1, 3, 4,   1, 4, 2
    ];
    switch (type) {
      case 4:
        const radius = scale * 2 * Math.sqrt(6) / 4;
        geometry = new THREE.TetrahedronGeometry(radius, 0);
        geometry.addGroup(0, 3, 0);
        geometry.addGroup(3, 3, 1);
        geometry.addGroup(6, 3, 2);
        geometry.addGroup(9, 3, 3);
        geometry.setAttribute("uv", new THREE.Float32BufferAttribute([
          0.9, 0.1,  0.5, 0.9,  0.1, 0.1,
          0.9, 0.1,  0.5, 0.9,  0.1, 0.1,
          0.9, 0.1,  0.5, 0.9,  0.1, 0.1,
          0.9, 0.1,  0.5, 0.9,  0.1, 0.1
        ], 2));
        geometry.rotateY(Math.PI / 4);
        geometry.rotateX(Math.atan(Math.sqrt(2)));
        faceValues = [3, 2, 1, 4];  // 이 순서를 주사위 텍스처와 일치하도록 조정
        break;
      case 6:
        geometry = new THREE.BoxGeometry(scale, scale, scale);
        geometry.addGroup(0, 6, 0);
        geometry.addGroup(6, 6, 1);
        geometry.addGroup(12, 6, 2);
        geometry.addGroup(18, 6, 3);
        geometry.addGroup(24, 6, 4);
        geometry.addGroup(30, 6, 5);
        faceValues = [2, 5, 1, 6, 3, 4];
        break;
        case 8:
          geometry = new THREE.OctahedronGeometry(scale);
          this.setOctahedronUVs(geometry);
          this.setOctahedronGroups(geometry);
          faceValues = [1, 8, 2, 7, 4, 5, 3, 6];
          break;
        case 10:
        case 101:
        case 102:
          geometry = this.createD10Geometry(scale);
          this.setD10UVs(geometry);
          this.setD10Groups(geometry);
          faceValues = type === 10 ? [1, 2, 3, 4, 5, 6, 7, 8, 9, 10] :
                        type === 101 ? [0, 10, 20, 30, 40, 50, 60, 70, 80, 90] :
                        [0, 1, 2, 3, 4, 5, 6, 7, 8, 9];
          // console.log('D10 geometry:', geometry);
          // console.log('D10 face values:', faceValues);
          break;
          case 12:
            geometry = new THREE.DodecahedronGeometry(scale);
            if (!geometry.index) {
              const indices = [];
              for (let i = 0; i < geometry.attributes.position.count; i += 3) {
                indices.push(i, i + 1, i + 2);
              }
              geometry.setIndex(indices);
            }
            this.setD12UVs(geometry);
            this.setD12Groups(geometry);
            faceValues = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
            // console.log('D12 geometry:', geometry);
            // console.log('D12 face values:', faceValues);
            break;

      case 20:
        geometry = new THREE.IcosahedronGeometry(scale);
        if (!geometry.index) {
          const indices = [];
          for (let i = 0; i < geometry.attributes.position.count; i += 3) {
            indices.push(i, i + 1, i + 2);
          }
          geometry.setIndex(indices);
        }
        this.setD20UVs(geometry);
        this.setD20Groups(geometry);
        faceValues = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20];
        // console.log('D20 geometry:', geometry);
        // console.log('D20 face values:', faceValues);
        break;

      default:
        throw new Error(`Unsupported dice type: ${type}`);
    }

    if (!geometry.index) {
      const indices = [];
      const positionAttribute = geometry.getAttribute('position');
      for (let i = 0; i < positionAttribute.count; i += 3) {
        indices.push(i, i + 1, i + 2);
      }
      geometry.setIndex(indices);
    }
  
    const materials = this.loadTextures(type);

    if (Array.isArray(materials)) {
      diceMesh = new THREE.Mesh(geometry, materials);
    } else {
      diceMesh = new THREE.Mesh(geometry, materials);
    }

// console.log(D${type} materials:, materials);
  
    diceMesh.userData.faceValues = faceValues;
    if (type === 6) {
      const edgesGeometry = new THREE.EdgesGeometry(geometry);
      const wireframe = new THREE.LineSegments(edgesGeometry, new THREE.LineBasicMaterial({ color: 0x000000, linewidth: 2 }));
      diceMesh.add(wireframe);
    } else {
      const wireframe = new THREE.WireframeGeometry(geometry);
      const line = new THREE.LineSegments(wireframe);
      line.material.depthTest = false;
      line.material.opacity = 0.25;
      line.material.transparent = true;
      diceMesh.add(line);
    }

    const shape = this.createConvexPolyhedron(geometry);
    const mass = 1;
    const body = new CANNON.Body({ mass, shape });
    // console.log('Created dice mesh:', diceMesh);
    // console.log('Dice materials:', diceMesh.material);
    return { diceMesh, body };
  }

  setTetrahedronUVs(geometry) {
    const uvs = [
      0, 0,  0.5, 1,  1, 0,
      0, 0,  0.5, 1,  1, 0,
      0, 0,  0.5, 1,  1, 0,
      0, 0,  0.5, 1,  1, 0
    ];
    geometry.setAttribute('uv', new THREE.Float32BufferAttribute(uvs, 2));
    geometry.attributes.uv.needsUpdate = true;
    return geometry;
  }

  createTetrahedronMesh(geometry, materials) {
    const mesh = new THREE.Mesh(geometry, materials);
    mesh.material = materials;
    return mesh;
  }
  setOctahedronUVs(geometry) {
    const uvs = [
      0.5, 1, 0.06698729810778059, 0.25, 0.9330127018922194, 0.25,
      0.06698729810778059, 0.75, 0.5, 0, 0.9330127018922194, 0.75,
      0.5, 0, 0.9330127018922194, 0.75, 0.06698729810778059, 0.75,
      0.9330127018922194, 0.25, 0.5, 1, 0.06698729810778059, 0.25,
      0.5, 1, 0.06698729810778059, 0.25, 0.9330127018922194, 0.25,
      0.06698729810778059, 0.75, 0.5, 0, 0.9330127018922194, 0.75,
      0.5, 0, 0.9330127018922194, 0.75, 0.06698729810778059, 0.75,
      0.9330127018922194, 0.25, 0.5, 1, 0.06698729810778059, 0.25
    ];
    geometry.setAttribute('uv', new THREE.Float32BufferAttribute(uvs, 2));
  }
  setOctahedronGroups(geometry) {
    const faces = geometry.attributes.position.count / 3;
    let groupStart = 0;
    for (let i = 0; i < faces; i++) {
      geometry.addGroup(groupStart, 3, i);
      groupStart += 3;
    }
  }
  // 4. 10면체 주사위 기하학 생성
  createD10Geometry() {
    const geometry = new THREE.BufferGeometry();
    const vertices = [
      0, 1, 0,
    1, 0, 0,
    0.30901699437494745, 0, 0.9510565162951535,
    -0.8090169943749473, 0, 0.5877852522924732,
    -0.8090169943749476, 0, -0.587785252292473,
    0.30901699437494723, 0, -0.9510565162951536,
    0, -1, 0
    ];
    const indices = [
      0, 2, 1, 0, 3, 2, 0, 4, 3, 0, 5, 4, 0, 1, 5,
      6, 1, 2, 6, 2, 3, 6, 3, 4, 6, 4, 5, 6, 5, 1
    ];
  
    // // 상단 뿔의 꼭지점
    // vertices.push(0, 1, 0);
  
    // // 상단 뿔의 기저 꼭지점들
    // for (let i = 0; i < 5; i++) {
    //   const angle = (i * Math.PI * 2) / 5;
    //   const x = Math.cos(angle);
    //   const y = 0;
    //   const z = Math.sin(angle);
    //   vertices.push(x, y, z);
    // }
  
    // // 하단 뿔의 꼭지점
    // vertices.push(0, -1, 0);
  
    // // 상단 뿔의 면 (반시계 방향으로 수정)
    // for (let i = 1; i <= 5; i++) {
    //   indices.push(0, i % 5 + 1, i);
    // }
  
    // // 하단 뿔의 면 (반시계 방향으로 수정)
    // for (let i = 1; i <= 5; i++) {
    //   indices.push(6, i, i % 5 + 1);
    // }
  
    geometry.setAttribute('position', new THREE.Float32BufferAttribute(vertices, 3));
    geometry.setIndex(indices);
    geometry.computeVertexNormals();

    // console.log("D10 Vertices:", vertices);
    // console.log("D10 Indices:", indices);
  
    return geometry;
  }

  
  // 5. 주사위 데이터 추출, 정점 변환, 면 변환
  createConvexPolyhedron(geometry) {
    const vertices = Array.from(geometry.attributes.position.array);
    const indices = Array.from(geometry.index.array);
    const cannonVertices = [];

    for (let i = 0; i < vertices.length; i += 3) {
      cannonVertices.push(new CANNON.Vec3(vertices[i], vertices[i + 1], vertices[i + 2]));
    }
    const cannonFaces = [];
    for (let i = 0; i < indices.length; i += 3) {
      cannonFaces.push([indices[i], indices[i + 1], indices[i + 2]]);
    }
  
    return new CANNON.ConvexPolyhedron(cannonVertices, cannonFaces);
  }
 // 6. 주사위 정점 최적화
  createIndexedBufferGeometry(geometry) {
  
    const nonIndexed = geometry.toNonIndexed();
    const position = nonIndexed.attributes.position;
    const indices = [];
  
    for (let i = 0; i < position.count; i++) {
      indices.push(i);
    }
  
    nonIndexed.setIndex(indices);
    return nonIndexed;
  }

   // 7. 주사위 면 설정
   getTopFaceValue(diceMesh) {
    const geometryType = diceMesh.geometry.type;
    switch (geometryType) {
      case 'BoxGeometry':
        return this.getD6TopFaceValue(diceMesh);
      case 'TetrahedronGeometry':
        return this.getD4TopFaceValue(diceMesh);
      case 'OctahedronGeometry':
      case 'PolyhedronGeometry':
        return this.getD8TopFaceValue(diceMesh);
      case 'BufferGeometry': // D10 uses custom BufferGeometry
        return this.getD10TopFaceValue(diceMesh);
      case 'DodecahedronGeometry':
        return this.getD12TopFaceValue(diceMesh);
      case 'IcosahedronGeometry':
        return this.getD20TopFaceValue(diceMesh);
      default:
        console.error('Unknown dice type:', geometryType);
        return undefined;
    }
  }

  getD6TopFaceValue(diceMesh) {
    const normals = [
      new THREE.Vector3(0, 1, 0),
      new THREE.Vector3(0, -1, 0),
      new THREE.Vector3(1, 0, 0),
      new THREE.Vector3(-1, 0, 0),
      new THREE.Vector3(0, 0, 1),
      new THREE.Vector3(0, 0, -1)
    ];
    const faceValues = diceMesh.userData.faceValues;
    return this.getTopFaceValueByNormals(diceMesh, normals, faceValues);
  }


  getD4TopFaceValue(diceMesh) {
    const vertices = diceMesh.geometry.attributes.position.array;
    const faceValues = diceMesh.userData.faceValues; // 각 면의 값들
  
    const faces = [
      [0, 3, 1],
      [0, 1, 2],
      [0, 2, 3],
      [1, 3, 2]
    ];
  
    let lowestPoint = Infinity;
    let lowestFaceIndex = -1;
  
    faces.forEach((face, index) => {
      const v1 = new THREE.Vector3(vertices[face[0] * 3], vertices[face[0] * 3 + 1], vertices[face[0] * 3 + 2]);
      const v2 = new THREE.Vector3(vertices[face[1] * 3], vertices[face[1] * 3 + 1], vertices[face[1] * 3 + 2]);
      const v3 = new THREE.Vector3(vertices[face[2] * 3], vertices[face[2] * 3 + 1], vertices[face[2] * 3 + 2]);
  
      const center = new THREE.Vector3()
        .add(v1)
        .add(v2)
        .add(v3)
        .divideScalar(3);
  
      const transformedCenter = center.clone().applyMatrix4(diceMesh.matrixWorld);
  
      if (transformedCenter.y < lowestPoint) {
        lowestPoint = transformedCenter.y;
        lowestFaceIndex = index;
      }
    });  
    return faceValues[lowestFaceIndex];
  }
  



  getD4BottomFaceValue(diceMesh) {
    const normals = [
      new THREE.Vector3(1, 1, 1).normalize(),
      new THREE.Vector3(-1, -1, 1).normalize(),
      new THREE.Vector3(-1, 1, -1).normalize(),
      new THREE.Vector3(1, -1, -1).normalize()
    ];
    const faceValues = diceMesh.userData.faceValues;
    let minDot = Infinity;
    let bottomFaceIndex = -1;
  
    normals.forEach((normal, index) => {
      const transformedNormal = normal.clone().applyQuaternion(diceMesh.quaternion);
      // 여기서 (0, -1, 0)을 사용하여 아래 방향과의 내적을 계산합니다.
      const dot = transformedNormal.dot(new THREE.Vector3(0, -1, 0));
      if (dot < minDot) {
        minDot = dot;
        bottomFaceIndex = index;
      }
    });
  
    return faceValues[bottomFaceIndex];
  }

  getD8TopFaceValue(diceMesh) {
    const normals = [
      new THREE.Vector3(1, 1, 1),
      new THREE.Vector3(-1, 1, 1),
      new THREE.Vector3(1, -1, 1),
      new THREE.Vector3(-1, -1, 1),
      new THREE.Vector3(1, 1, -1),
      new THREE.Vector3(-1, 1, -1),
      new THREE.Vector3(1, -1, -1),
      new THREE.Vector3(-1, -1, -1)
    ];
    const faceValues = diceMesh.userData.faceValues;
    return this.getTopFaceValueByNormals(diceMesh, normals, faceValues);
  }

  getD10TopFaceValue(diceMesh) {
    const normals = [];
    for (let i = 0; i < 10; i++) {
      const angle = (i * Math.PI * 2) / 10;
      normals.push(new THREE.Vector3(Math.cos(angle), 0.5, Math.sin(angle)));
    }
    const faceValues = diceMesh.userData.faceValues;
    return this.getTopFaceValueByNormals(diceMesh, normals, faceValues);
  }

  setD10UVs(geometry) {
    const uvs = [];
    const faceUVs = [
      [0.5, 1, 0, 0.35, 1, 0.35],
      [0.5, 1, 0, 0.35, 1, 0.35],
      [0.5, 1, 0, 0.35, 1, 0.35],
      [0.5, 1, 0, 0.35, 1, 0.35],
      [0.5, 1, 0, 0.35, 1, 0.35],
      [0.5, 0, 0, 0.65, 1, 0.65],
      [0.5, 0, 0, 0.65, 1, 0.65],
      [0.5, 0, 0, 0.65, 1, 0.65],
      [0.5, 0, 0, 0.65, 1, 0.65],
      [0.5, 0, 0, 0.65, 1, 0.65]
    ];
  
    for (let i = 0; i < 10; i++) {
      uvs.push(...faceUVs[i]);
    }
  
    geometry.setAttribute('uv', new THREE.Float32BufferAttribute(uvs, 2));
  }

  setD10Groups(geometry) {
    const faces = geometry.index.count / 3;
    let groupStart = 0;
    for (let i = 0; i < faces; i++) {
      geometry.addGroup(groupStart, 3, i);
      groupStart += 3;
    }
    // console.log("D10 Groups:", geometry.groups);
  }

  getD12TopFaceValue(diceMesh) {
    const phi = (1 + Math.sqrt(5)) / 2;
    const normals = [
      new THREE.Vector3(1, 1, 1),
      new THREE.Vector3(1, 1, -1),
      new THREE.Vector3(1, -1, 1),
      new THREE.Vector3(1, -1, -1),
      new THREE.Vector3(-1, 1, 1),
      new THREE.Vector3(-1, 1, -1),
      new THREE.Vector3(-1, -1, 1),
      new THREE.Vector3(-1, -1, -1),
      new THREE.Vector3(0, 1/phi, phi),
      new THREE.Vector3(0, -1/phi, phi),
      new THREE.Vector3(0, 1/phi, -phi),
      new THREE.Vector3(0, -1/phi, -phi)
    ];
    const faceValues = diceMesh.userData.faceValues;
    return this.getTopFaceValueByNormals(diceMesh, normals, faceValues);
  }

  setD12UVs(geometry) {
    const uvs = [];
    const positions = geometry.attributes.position.array;
    for (let i = 0; i < positions.length; i += 9) {
      uvs.push(0, 0, 1, 0, 0.5, 1);
    }
    geometry.setAttribute('uv', new THREE.Float32BufferAttribute(uvs, 2));
    // console.log('D12 UVs set');
  }
  
  setD12Groups(geometry) {
    if (!geometry.index) {
      console.error('Geometry index is null for D12');
      return;
    }
    const faces = geometry.index.count / 3;
    let groupStart = 0;
    for (let i = 0; i < faces; i++) {
      geometry.addGroup(groupStart, 3, i % 12);
      groupStart += 3;
    }
    // console.log('D12 Groups:', geometry.groups);
  }



  getD20TopFaceValue(diceMesh) {
    const phi = (1 + Math.sqrt(5)) / 2;
    const normals = [
      new THREE.Vector3(0, 1, phi),
      new THREE.Vector3(0, -1, phi),
      new THREE.Vector3(0, 1, -phi),
      new THREE.Vector3(0, -1, -phi),
      new THREE.Vector3(1, phi, 0),
      new THREE.Vector3(-1, phi, 0),
      new THREE.Vector3(1, -phi, 0),
      new THREE.Vector3(-1, -phi, 0),
      new THREE.Vector3(phi, 0, 1),
      new THREE.Vector3(phi, 0, -1),
      new THREE.Vector3(-phi, 0, 1),
      new THREE.Vector3(-phi, 0, -1),
      new THREE.Vector3(1, 1, 1),
      new THREE.Vector3(-1, 1, 1),
      new THREE.Vector3(1, -1, 1),
      new THREE.Vector3(-1, -1, 1),
      new THREE.Vector3(1, 1, -1),
      new THREE.Vector3(-1, 1, -1),
      new THREE.Vector3(1, -1, -1),
      new THREE.Vector3(-1, -1, -1)
    ];
    const faceValues = diceMesh.userData.faceValues;
    return this.getTopFaceValueByNormals(diceMesh, normals, faceValues);
  }

  setD20Groups(geometry) {
    if (!geometry.index) {
      console.error('Geometry index is null for D20');
      return;
    }
    const faces = geometry.index.count / 3;
    let groupStart = 0;
    for (let i = 0; i < faces; i++) {
      geometry.addGroup(groupStart, 3, i);
      groupStart += 3;
    }
    // console.log('D20 Groups:', geometry.groups);
  }


  setD20UVs(geometry) {
    const uvs = [];
    const positions = geometry.attributes.position.array;
    for (let i = 0; i < positions.length; i += 9) {
      uvs.push(0, 0, 1, 0, 0.5, 1);
    }
    geometry.setAttribute('uv', new THREE.Float32BufferAttribute(uvs, 2));
    // console.log('D20 UVs set');
  }

  getTopFaceValueByNormals(diceMesh, normals, faceValues) {
    let maxDot = -Infinity;
    let topFaceIndex = -1;

    normals.forEach((normal, index) => {
      const transformedNormal = normal.clone().applyQuaternion(diceMesh.quaternion);
      const dot = transformedNormal.dot(new THREE.Vector3(0, 1, 0));
      if (dot > maxDot) {
        maxDot = dot;
        topFaceIndex = index;
      }
    });

    return faceValues[topFaceIndex];
  }
// 8. 주사위 텍스처 로드
loadTextures(type) {
  const loader = new THREE.TextureLoader();
  const textures = [];
  const faceOrder = {
    4: [134,312,142,432],
    6: [1, 6, 2, 5, 3, 4],
    8: [1, 2, 3, 4, 5, 6, 7, 8],
    10: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    12: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12],
    20: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20],
    101 : [0, 10, 20, 30, 40, 50, 60, 70, 80, 90],
    102 : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
  };

  const order = faceOrder[type] || [];
  // console.log(Loading textures for type ${type}:, order);
  const adjustTexture = (texture, settings) => {
    texture.repeat.set(settings.repeat.x, settings.repeat.y);
    texture.center.set(settings.center.x, settings.center.y);
    texture.offset.set(settings.offset.x, settings.offset.y);
    texture.rotation = settings.rotation;
  };

  switch(type) {
    case 4:
      for (let i = 0; i < order.length; i++) {
        const texture = loader.load(
          `/assets/dice/dd${order[i]}.png`,
          (loadedTexture) => {
            adjustTexture(loadedTexture, {
              repeat: { x: 1.1, y: 1.1 },
              center: { x: 0.5, y: 0.5 },
              offset: { x: 0, y: 0.15 },
              rotation: 0
            });
          },
          undefined,
          (error) => console.error(`Error loading texture ${order[i]}:`, error)
        );
        textures.push(texture);
      }
      break;
    case 6:
      for (let i = 0; i < order.length; i++) {
        const texture = loader.load(
          `/assets/dice/dd${order[i]}.png`,
          (loadedTexture) => {
            adjustTexture(loadedTexture, {
              repeat: { x: 0.8, y: 0.8 },
              center: { x: 0.5, y: 0.5 },
              offset: { x: 0, y: 0 },
              rotation: 0
            });
          },
          undefined,
          (error) => console.error(`Error loading texture ${order[i]}:`, error)
        );
        textures.push(texture);
      }
      break;
    case 8:
      for (let i = 0; i < order.length; i++) {
        const texture = loader.load(
          `/assets/dice/dd${order[i]}.png`,
          (loadedTexture) => {
            adjustTexture(loadedTexture, {
              repeat: { x: 1.2, y: 1.2 },
              center: { x: 0.5, y: 0.5 },
              offset: { x: 0, y: 0.05 },
              rotation: 0
            });
          },
          undefined,
          (error) => console.error(`Error loading texture ${order[i]}:`, error)
        );
        textures.push(texture);
      }
      break;
    case 10:
    case 101:
    case 102:
      return this.createDebugTexturesForD10(type);
    case 12:
      return this.createDebugTexturesForD12();
    case 20:
      return this.createDebugTexturesForD20();
    default:
      console.error('Unsupported dice type:', type);
      return new THREE.MeshBasicMaterial({ 
        color: 0xFFFFFF,
        side: THREE.DoubleSide
      });
  }

  return textures.map(texture => new THREE.MeshBasicMaterial({ 
    map: texture,
    side: THREE.DoubleSide
  }));
}


  createDebugTexturesForD10(type) {
    const materials = [];
    const colors = [
      '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF',
      '#00FFFF', '#FFA500', '#800080', '#008000', '#FFC0CB'
    ];
  
    for (let i = 0; i < 10; i++) {
      const canvas = document.createElement('canvas');
      canvas.width = canvas.height = 128;
      const ctx = canvas.getContext('2d');
  
      // 배경색 채우기
      ctx.fillStyle = colors[i];
      ctx.fillRect(0, 0, 128, 128);
  
      // 텍스트 그리기
      ctx.fillStyle = 'white';
      ctx.font = 'bold 64px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      let value;
      if (type === 10) {
        value = i + 1;
      } else if (type === 101) {
        value = i * 10;
      } else { // type === 102
        value = i;
      }
      ctx.fillText(value.toString(), 64, 64);
  
      // 캔버스를 텍스처로 변환
      const texture = new THREE.CanvasTexture(canvas);
      const material = new THREE.MeshBasicMaterial({
        map: texture,
        side: THREE.DoubleSide
      });
      materials.push(material);
    }
  
    return materials;
  }

  createDebugTexturesForD12() {
    const materials = [];
    const colors = [
      '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF',
      '#00FFFF', '#FFA500', '#800080', '#008000', '#FFC0CB',
      '#A52A2A', '#DEB887'
    ];
  
    for (let i = 0; i < 12; i++) {
      const canvas = document.createElement('canvas');
      canvas.width = canvas.height = 128;
      const ctx = canvas.getContext('2d');
  
      // 배경색 채우기
      ctx.fillStyle = colors[i];
      ctx.fillRect(0, 0, 128, 128);
  
      // 텍스트 그리기
      ctx.fillStyle = 'white';
      ctx.font = 'bold 64px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText((i + 1).toString(), 64, 64);
  
      // 캔버스를 텍스처로 변환
      const texture = new THREE.CanvasTexture(canvas);
      const material = new THREE.MeshBasicMaterial({
        map: texture,
        side: THREE.DoubleSide
      });
      materials.push(material);
    }
  
    return materials;
  }


  createDebugTexturesForD20() {
    const materials = [];
    const colors = [
      '#FF0000', '#00FF00', '#0000FF', '#FFFF00', '#FF00FF',
      '#00FFFF', '#FFA500', '#800080', '#008000', '#FFC0CB',
      '#A52A2A', '#DEB887', '#5F9EA0', '#7FFF00', '#D2691E',
      '#FF7F50', '#6495ED', '#FFF8DC', '#DC143C', '#00008B'
    ];
  
    for (let i = 0; i < 20; i++) {
      const canvas = document.createElement('canvas');
      canvas.width = canvas.height = 128;
      const ctx = canvas.getContext('2d');
  
      // 배경색 채우기
      ctx.fillStyle = colors[i];
      ctx.fillRect(0, 0, 128, 128);
  
      // 텍스트 그리기
      ctx.fillStyle = 'white';
      ctx.font = 'bold 64px Arial';
      ctx.textAlign = 'center';
      ctx.textBaseline = 'middle';
      ctx.fillText((i + 1).toString(), 64, 64);
  
      // 캔버스를 텍스처로 변환
      const texture = new THREE.CanvasTexture(canvas);
      const material = new THREE.MeshBasicMaterial({
        map: texture,
        side: THREE.DoubleSide
      });
      materials.push(material);
    }
  
    return materials;
  }

}

export default ThreeJSManager;