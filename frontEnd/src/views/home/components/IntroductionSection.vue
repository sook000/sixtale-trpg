<template>
    <section class="introduction">
      <div class="container">
        <h1>TRPG란?</h1>
        <br/><br/>
        <p class="intro-description">
          TRPG(테이블탑 롤플레잉 게임)은 플레이어들이 각자 캐릭터를 맡아 이야기를 만들어가는 협력형 게임입니다.<br/>
          주사위 굴리기와 규칙에 따라 진행되며, 상상력과 창의력이 중요한 요소입니다.
        </p>
        <div class="feature">
          <div class="feature-text">
            <h3>다양한 세계관</h3>
            <br/>
            <p>
              TRPG는 다양한 세계관을 탐험할 수 있는 기회를 제공합니다. <br/>판타지, 공상과학, 역사적 배경 등 다양한
              설정에서 모험을 즐길 수 있습니다.
            </p>
          </div>
          <div class="feature-image">
            <img src="@/assets/images/introduction/worlds.png" alt="다양한 세계관" />
          </div>
        </div>
        <div class="feature">
          <div class="feature-image">
            <img src="@/assets/images/introduction/scenario.png" alt="나만의 시나리오" />
          </div>
          <div class="feature-text">
            <h3>나만의 시나리오</h3>
            <br/>
            <p>
              직접 시나리오를 작성하고, <br/>플레이어들과 함께 만들어가는 나만의 이야기를 경험해보세요.<br/>
              창의력을 발휘하여 독창적인 모험을 디자인할 수 있습니다.
            </p>
          </div>
        </div>
        <div class="feature">
          <div class="feature-text">
            <h3>몰입감</h3>
            <br/>
            <p>
              TRPG는 몰입감을 극대화시켜줍니다.<br/> 캐릭터의 역할을 연기하며, <br/> 다른 플레이어들과 함께 생생한 이야기 속으로
              빠져들 수 있습니다.
            </p>
          </div>
          <div class="feature-image">
            <img src="@/assets/images/introduction/immersion.png" alt="몰입감" />
          </div>
        </div>
      </div>
    </section>
  </template>
  
  <script setup>
  </script>
  
  <style scoped>
  .introduction {
   background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%), linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
  color: white;
  padding-right: 0;
  padding-bottom: 0;
  padding-left: 0;
}

  .container {
    padding: 40px 20px; 
    border-radius: 0; 
    max-width: 1200px; 
    margin: 0 auto;
    text-align: center;
    padding-top: 150px;
  }
  
  h2 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .intro-description {
    text-align: center;
    font-size: larger;
    margin-bottom: 90px;
  }
  
  .feature {
    display: flex;
    align-items: center;
    margin-bottom: 40px;
  }
  
  .feature-text {
    flex: 1;
    padding: 20px;
  }
  
  .feature-image {
    flex: 1;
    text-align: center;
  }
  
  .feature-image img {
    max-width: 100%;
    border-radius: 10px;
  }
  
  h3 {
    margin-bottom: 10px;
  }
  </style>

