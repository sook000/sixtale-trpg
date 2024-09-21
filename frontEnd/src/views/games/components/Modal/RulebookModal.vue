<template>
  <div v-if="isOpen" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content" :style="modalStyle">
      <div class="rulebook-container">
      <div class="modal-header">
        <!-- <h2>룰북</h2> -->
      </div>
      <div class="modal-body">
        <div class="rulebook">
          <div class="tabs">
            <div
              class="tab"
              v-for="(tab, index) in tabs"
              :key="index"
              @click="selectTab(index)"
            >
              <img :src="index === selectedTab ? activeTabImage : tab.image" />
              <span>{{ tab.label }}</span>
            </div>
          </div>
          <div class="page-content">
            <transition name="page-flip" mode="out-in">
              <div class="page left" :key="selectedTab + '-' + currentPage">
                <!-- 첫 페이지는 이미지로 표시 -->
                <div v-if="selectedTab === 0 && currentPage === 0">
                  <img
                    :src="firstPageImage"
                    alt="First Page"
                    class="first-page-image"
                  />
                </div>
                <!-- 나머지 페이지는 텍스트로 표시 -->
                <p
                  v-for="(content, index) in leftPages[selectedTab]"
                  :key="index"
                  v-show="currentPage === index"
                  v-html="content"
                  class="page-text"
                ></p>
              </div>
            </transition>
            <transition name="page-flip" mode="out-in">
              <div class="page right" :key="selectedTab + '-' + currentPage">
                <div v-if="selectedTab === 0 && currentPage === 0" class="table-of-contents">
                  <p v-html="rightPages[selectedTab][currentPage]" class="page-text toc-text"></p>
                </div>
                <p
                  v-else
                  v-if="currentPage < rightPages[selectedTab].length"
                  v-html="rightPages[selectedTab][currentPage]"
                  class="page-text"
                ></p>
              </div>
            </transition>
            <!-- 네비게이션 버튼 -->
            <button
              v-if="currentPage > 0"
              @click="prevPage"
              class="nav-button prev"
            >
              <img :src="prevArrow" alt="Previous" />
            </button>
            <button
              v-if="currentPage < maxPage - 1"
              @click="nextPage"
              class="nav-button next"
            >
              <img :src="nextArrow" alt="Next" />
            </button>
          </div>
        </div>
      </div>
      <!-- <div class="modal-footer">
        <button @click="closeModal">닫기</button>
      </div> -->
    </div>
  </div>
</div>
</template>

<script setup>
import { ref, computed } from "vue";

const props = defineProps({
  isOpen: Boolean,
});

const emit = defineEmits(["close"]);

const closeModal = () => {
  emit("close");
};

// 이미지 경로 설정
const rulebookImage = require("@/assets/images/room/rulebook_modal/book.png");
const tabImage = require("@/assets/images/room/rulebook_modal/vector.png");
const activeTabImage = require("@/assets/images/room/rulebook_modal/active_vector.png");
const prevArrow = require("@/assets/images/room/rulebook_modal/Arrow_Down.png");
const nextArrow = require("@/assets/images/room/rulebook_modal/Arrow_Up.png");
const firstPageImage = require("@/assets/images/room/rulebook_modal/scenario_main.jpg"); // 첫 번째 왼쪽 페이지 이미지 경로

// 탭 설정
const tabs = [
  { label: "서문", image: tabImage },
  { label: "게임의 기본", image: tabImage },
  { label: "직업과 역할", image: tabImage },
  { label: "게임 시스템", image: tabImage },
  { label: "게임 환경", image: tabImage },
  { label: "게임 진행", image: tabImage },
  { label: "부록 및 색인", image: tabImage },
];

const selectedTab = ref(0); // 기본 선택된 탭
const currentPage = ref(0); // 현재 페이지 번호

// 페이지 내용 설정
const leftPages = [
  [
    "",
    "신과 악마, 질서와 혼돈, 선과 악, 검과 마법이 살아 숨 쉬는 모험과 환상의 세계가 있습니다. 용감한 영웅들이 부귀와 영광을 찾아 이 세계의 구석구석으로 위험천만한 여행을 떠납니다. 이것이 던전월드의 세계입니다.<br><br>던전월드의 모험가들은 다양한 모습을 하고 있습니다. 영웅은 엘프, 인간, 드워프, 하플링을 가리지 않고 나옵니다. 어떤 이들은 철갑을 두른 전쟁의 화신이고, 또 어떤 이들은 신비롭고도 강한 마법의 힘을 불러내어 뜻대로 부릴 줄 압니다. 거룩한 사제도, 음흉한 도적도, 고결한 성기사도, 이 세계에서는 보물과 영광을 마다하지 않습니다.<br><br>영웅 노릇이 항상 고귀하고 쉬운 것은 아닙니다. 사냥꾼이 친구들을 이끌고 지나는 그 오래된 숲 속에는 사람 머리 정도는 한 입에 잘라 먹을 수 있는 괴물이 적어도 백은 됩니다. 고블린 노예상인들이 한 무더기 숨어 있을 수도 있습니다.",
    "도적<br><br>모닥불 주변에서 친구들이 떠드는 소리는 여러 번 들었습니다. 이 싸움 저 싸움에서 자기가 얼마나 용맹했는지 자랑하기도 하고, 신들께서 일행을 보우하신다고 소리를 높이기도 합니다. 그 사이에 당신은 돈을 세면서 씨익 웃습니다. 이것이야말로 모든 것을 초월하는 삶의 보람입니다. 이 세상에서 진짜 중요한 것이 다름 아닌 돈임을 당신은 잘 알고 있습니다.<br><br>당신이 혼자 슬쩍 빠져나갈 때마다 동료들은 의심하며 타박하지만, 당신이 없었으면 모두 벽에서 발사된 칼날에 해부가 되었거나 고대인들의 독침 덫에 걸려 길고 괴로운 죽음을 맞았을 것입니다. 그러니 마음껏 불평불만을 하라고 내버려 두십시오. 모험질이 끝나면 영웅님들의 무덤에 건배를 올리는 것은 당신이 될 테니까요.<br><br>그리고 그 건배는 창고에 황금이 가득한 저택의 비단의자에서 올려야 맛이겠지요.",
    "마법사<br><br>이 세계는 법칙으로 움직입니다. 그 법칙은 사람의 법률도 아니고, 어느 좀스러운 폭군의 변덕도 아닙니다. 그보다 더 크고, 더 좋은 법칙입니다. 잡고 있던 물건을 놓으면 떨어지고, 무에서 유를 만들 수는 없으며, 죽은 것은 도로 살아나지 않습니다. 맞지요?그렇게라도 믿지 않으면 무서워서 잠을 잘 수가 없을 것입니다.<br><br>당신은 고서에 얼굴을 파묻고 오랜 시간을 보냈습니다. 실험 때문에 미칠 뻔한 적도 많고, 소환 의식이 잘못되어 영혼이 날아갈 뻔 한 적도 있습니다. 그 모든 것은 무엇을 위해서 한 것입니까? 당연히 힘을 위해 한 것입니다. 달리 추구할 것이 세상에 어디 있단 말입니까? 산 사람의 혈관에 흐르는 피를 삽시간에 끓어오르게 만드는 힘, 하늘에서 천둥을 부르고 땅을 흔드는 힘. 세상 사람들이 그렇게나 믿고 싶어하는 법칙들을 벗어던지는 힘. 그것이 마법입니다.",
    "사제<br><br>모험가들이 가는 곳은 살벌하기가 짝이 없어 마치 신의 버림을 받은 듯합니다. 시체가 돌아다니지를 않나, 식인 짐승이 들끓지를 않나, 번듯한 사원이 있는 문명의 땅과는 풍경도 딴판이고 거리도 멉니다. 그래서 당신이 이곳에 필요한 것입니다.<br><br>당신에게 있어서 이교도들에게 신의 영광을 보이는 것은 그냥 직무가 아니라 천부의 임무입니다. 검과 철퇴와 주문으로 말씀을 전파하고 무지한 야만의 땅에 깊은 칼자국을 내고 참된 믿음의 씨앗을 심는 것이야말로 곧 당신 삶의 보람입니다. 어떤 사람들은 말합니다, 신앙은 가슴에 품고 있을 때 진정으로 빛난다고. 당신은 그게 헛소리라는 것을 잘 압니다. 신은 칼끝에 계시기 때문입니다.<br><br>온 세상에 가르쳐 주십시오, 누가 우주의 진정한 주인이신지.",
    "음유시인<br><br>모험가의 인생은 탁 트인 길과 부귀영화, 그리고 모험뿐이라고, 노래와 시에서는 말합니다. 술집이면 술집마다, 여관이면 여관마다 울려 퍼지는 노래들이 설마 처음부터 끝까지 꾸며낸 소리겠습니까? 평민의 가슴과 왕족의 가슴을 가리지 않고 설레게 하는 노래들, 야수의 마음을 달래고 사람의 투지를 불태우는 노래들의 출처는 과연 어디겠습니까?<br>그게 부드러운 혀와 날카로운 재치의 음유시인, 노래와 이야기에 사는 자, 바로 당신입니다. 평범한 노래꾼은 들은 노래를 부르고 왼 이야기를 되풀이할 뿐이지만, 진정한 음유시인은 노래 같은 삶을 삽니다. 그러니 장화끈을 매고 단도의 날을 가십시오. 영웅들 틈에서 싸우며 영웅이 되어야 비로소 그런 노래를 지을 자격도 생기는 법입니다.<br><br>동료들이 당신의 노래를 기다립니다. 이제 출발할 시간입니다."
  ],
  [
    "던전월드는 대화로 이루어집니다. 누군가가 무슨 말인가를 하고, 다른 사람이 여기에 응답을 하고, 때로는 또 다른 사람이 끼어듭니다.<br><br>던전월드의 대화는 캐릭터들이 사는 세상과 캐릭터들 주변에서 일어나는 일들, 즉 “이야기”를 중심으로 이루어집니다.<br>이 책의 룰이 대화에 끼어들어서 세상에 변화를 일으키기도 합니다.<br>던전월드에는 “턴”이나 “순서”가 없고, 누가 언제 말을 할 수 있는지에 관한 규칙도 없습니다. 대화는 그냥 자연스럽게, 일상생활의 대화처럼 일어납니다.<br>던전월드에서는 아무도 긴 독백을 하지 않습니다. 플레이는 항상 오가는 대화로 이루어진다는 점을 기억하십시오.",
    "능력치와 능력수정치<br><br>많은 룰에서, 플레이어 캐릭터의 능력치와 능력수정치를 사용합니다. 능력치는 근력, 체력, 민첩성, 지능, 지혜, 매력의 여섯 가지이고, 3에서 18까지로 정의됩니다. 18은 사람으로서 최고 수준임을 가리킵니다.<br><br>판정을 할 때는 능력치에서 유래한 능력수정치가 사용됩니다. +근, +체, +민, +지, +혜, +매의 약자를 씁니다. 능력수정치는 -3에서 +3까지이고, 능력치를 따라서 정해집니다.",
    "캐릭터가 뭔가를 한다고 플레이어가 묘사를 했는데 그로 인해 특정 액션이 발동되면, 그 액션이 일어나고 그 액션의 룰이 적용됩니다. 액션이 판정을 요구하는 경우, 주사위를 어떻게 굴리며 그 결과를 어떻게 해석하는지도 명시되어 있습니다.<br><br>캐릭터가 이야기 속에서 액션의 발동 조건에 해당되는 일을 한 경우, 그 액션은 플레이어가 원하건 원치 않건 필히 발동됩니다. 예를 들어 자기 캐릭터가 도끼를 든 오크 옆을 지나 열린 문으로 나간다고 말했다면, 좋건 싫건 위험 돌파 액션이 발동됩니다. 위험 돌파의 발동 조건이 “즉각적인 위험을 감수하고서 행동하는 경우”이기 때문입니다. 액션 없이 오크를 지나간다고 할 수는 없습니다. 거꾸로 즉각적인 위험을 감수하거나 재난을 겪지 않은 상태에서는 위험 돌파 액션을 할 수도 없습니다. 액션과 이야기는 항상 일치해야 합니다."
  ],
  [
    "직업과 역할 내용 왼쪽1<br>더 많은 정보...",
    "직업과 역할 내용 왼쪽2<br>추가 설명...",
    "직업과 역할 내용 왼쪽3",
  ],
  ["게임 시스템 내용 왼쪽1<br>상세 내용", "게임 시스템 내용 왼쪽2"],
  ["게임 환경 내용 왼쪽1<br>세부사항", "게임 환경 내용 왼쪽2"],
  ["게임 진행 내용 왼쪽1<br>절차", "게임 진행 내용 왼쪽2"],
  ["부록 및 색인 내용 왼쪽1<br>참고", "부록 및 색인 내용 왼쪽2"],
];

const rightPages = [
  [
    "목차<br><br>1. 기본 소개<br>2. 게임의 기본<br>3. 직업과 역할<br>4. 게임 시스템<br>5. 게임 환경<br>6. 게임 진행<br>7. 부록 및 색인",
    "어쩌면 회색의 마녀가 사는 저주의 숲인지도? 원한에 찬 언데드들이 밤마다 깊은 무덤에서 깨어나 지나가는 사람을 잡아 간다고 해도 이상하지 않습니다. 세상은 이렇듯 무시무시하지만, 그곳에는 보물도 있습니다. 세상의 잊혀진 빈틈에 잠들어 이제 모두의 기억에서 사라진 황금과 보석과 마법은 상상을 초월할 정도로 많습니다. 굳센 영웅의 무리가 아니면 누가 이를 되찾겠습니까?<br><br>그 영웅의 무리가, 바로 당신과 친구들입니다. 다른 사람들이 갈 수 없는 곳, 감히 쳐다 보지도 못 하는 곳으로, 여러분은 뚜벅뚜벅 걸어갑니다. 여러분이 가는 곳에는 이루 말로 다 못할 정도로 끔찍한 괴물들이 있고, 그만큼이나 놀라운 보물들이 있습니다. 도전할 준비가 되었습니까?",
    "드루이드<br><br>모닥불 주위를 둘러 보십시오. 도시의 홍진에 뒤덮이고 땀 냄새가 진동하는 이 사람들 곁에 당신이 머무르는 이유는 무엇입니까? 마치 새끼곰들을 보살피는 어미곰처럼 친절하기 때문입니까? 그렇다면 이제 저 사람들도 당신과 한 무리인가요? 참으로 기묘한 형제자매를 두셨습니다. 이유가 무엇이건 간에, 당신의 날카로운 발톱, 그리고 그보다 날카로운 감각이 없으면 저들은 이곳에서 살아남지 못할 것입니다.<br><br>당신은 신성한 대지의 자녀입니다. 흙에서 태어나, 그 정령의 자국을 피부에 새겼습니다. 전에는 아니었을 수도 있습니다. 심지어 한때는 저들처럼 도시에서 살았을 수도 있습니다. 그러나 과거는 과거일 뿐입니다. 당신은 이제 그 형태조차 자유롭습니다. 당신 친구들은 돌을 깎아 만든 신들에게 기도하며 쇳조각에 윤을 냅니다. 자기들이 살던 그 악취 나는 도시로 돌아가면 영광이 기다릴 것이라고 합니다.",
    "사냥꾼<br><br>저 도회지 사람들을 보십시오. 늑대의 울음소리도 들어본 적이 없고, 동방의 사막에서 뜨거운 바람에 데워진 모래를 느껴본 적도 없습니다. 당신처럼 활과 단도로 사냥감을 잡아 본 적은 있을까요? 당연히 없습니다. 그러니까 당신을 필요로 했던 것입니다.<br><br>누구는 당신을 길잡이라고 합니다. 누구는 숲사람이라고, 누구는 야인이라고 합니다. 그러나 그런 말들은 야생의 몸과 마음을 담아낼 수 없습니다. 당신은 지금까지 혼자 야외에서 살아 왔지만, 위대한 무언가가, 어쩌면 운명이, 당신을 불러 이 도시 사람들과 함께 두었습니다. 이 사람들은 용감합니다. 강합니다. 하지만 이 사람들이 모르는 땅의 비밀을 당신은 알고 있습니다.<br><br>당신이 없으면 모두가 길을 잃습니다. 사냥꾼이여, 피와 어둠을 뚫고 길을 밝히십시오.",
    "성기사<br><br>지옥은 항상 저 아래에서 우리를 기다립니다. 불로 되어 있건, 얼음으로 되어 있건, 그 무엇으로 되어 있건 간에, 그 고통은 극단적이고 영원합니다. 영원한 구원과 영겁의 고문 사이에 버티고 선 것은 바로 당신, 당신뿐입니다. 사제도 신을 섬긴다고요? 전사도 갑옷을 입고 검을 다룬다고요? 사제는 자기 전에 하늘에 계신 신들께 기도를 올리겠지만, 전사도 “선”을 위하여 검을 집어 들 수 있지만, 당신은 압니다. 오로지 당신뿐이라는 것을.<br><br>신의 눈이자 손이요, 신이 내리는 최후의 일격, 그것이 바로 성기사이고, 그 힘은 정의와 미덕에서 옵니다. 다른 누구에게도 당신과 같은 순수한 의지는 없습니다.<br><br>그러니 성기사여, 이 무지한 자들을 인도하십시오. 거룩한 대의를 받들어, 이 속된 세계에 구원을 가져오십시오.정의가 필히 승리한다면, 패자는 그저 통곡하는 것이 옳지 않겠습니까?",
    "전사<br><br>매일 같이 갑옷과 칼솜씨만을 의지하며 뒤도 돌아보지 않고 위험 속에 뛰어들지만, 남들은 고마운 줄을 모릅니다. 전에 북스베르크의 술집에서 친구들 대신 옆구리에 단도를 맞았지만, 아무도 나팔을 불고 칭송해 주지 않습니다. 광기의 구덩이 가장자리에서 비명만 지르고 있던 사람들을 질질 끌어서 구해냈지만 그렇다고 천사들이 노래를 불러줄 것도 아닙니다.<br><br>당신이 이 일을 하는 건 뛰는 가슴과 싸움의 함성, 그리고 뜨겁고 뜨거운 피 때문입니다. 친구들은 강철로 만든 검을 가지고 다니지만, 전사여, 당신은 강철 그 자체입니다. 동료들은 야영지 모닥불에서 상처가 쑤신다고 불평하지만, 당신은 흉터를 드러내고 자랑합니다.<br><br>당신은 벽입니다. 무엇이 되었건 부딪쳐 보라고 하십시오. 먼지가 걷히고 나면 서 있는 것은 당신뿐일 테니까요."
  ],
  [
    "이 책에 실린 룰은 그 대화에 영향을 줍니다. 이야기 속에서 뭔가 특정한 일이 일어날 때 룰이 발동되고, 그러고 나면 대화에 그 내용이 반영되는 것입니다.<br><br>대화가 다 그렇듯, 말을 하는 것만큼이나 듣는 것도 중요합니다. 대화를 통해 플레이에서 확립된 사실들, 예를 들어 세계 설정이나 상황 묘사는 자기가 그 대화에 직접 끼지 않았다 해도 의미가 있습니다. 어떤 액션을 할 수 있는지가 달라질 수도 있고, 기회가 생길 수도 있으며, 극복해야 할 장애물이 나타날 수도 있기 때문입니다. 던전월드의 대화는 모두가 서로에게 귀를 기울이고, 질문을 하고, 오가는 말을 쌓아 나갈 때 가장 재미있습니다.<br><br>",
    "액션<br><br>액션은 던전월드의 룰에서 가장 기본적인 단위입니다. 다음과 같은 형식으로 정의됩니다: <br>근거리 전투에서 적을 공격하면 +근 판정을 합니다. 10+이면 적에게 피해를 주고 자기는 공격을 피합니다. 원하면 적에게 빈틈을 보이고, 대신에 피해를 1d6 더 줄 수도 있습니다. 7~9이면 적에게 피해를 주고, 자신도 상대의 공격을 받습니다.<br><br>액션에는 발동 조건과 효과가 정해져 있습니다. 즉, 이야기 속에서 그 액션의 발동 조건에 해당되는 일이 일어나면 액션이 발동되고, 액션이 발동되면 이야기 속에 정해진 효과가 일어납니다. 여기서 “이야기 속”이라는 말은, 액션을 일으키는 일도 액션의 효과도 캐릭터들이 사는 세상에서 일어나는 일이라는 뜻입니다. 위의 액션에서 발동 조건은 “근거리 전투에서 적을 공격하면”입니다. 그 뒤에 나오는 것이 효과이며, 이 액션의 효과는 주사위를 굴리고 수정치를 더한 결과, 즉 판정 결과에 따라 달라지게 되어 있습니다.",
    "이야기 속에서 일어난 일이 액션의 발동 조건에 해당하는지는 놓치기 쉬우니 모두가 눈여겨 보십시오. 액션이 발동되었는지 불확실한 순간이 있으면, 모두가 의논해서 상황을 확실하게 규정하는 것이 좋습니다. 모두가 현재 상황을 똑같이 이해한 뒤에 진행을 계속합니다.<br><br>마스터가 만든 괴물과 NPC 등등도 액션을 할 수 있지만, PC (플레이어 캐릭터)의 액션과는 작동 방식이 다릅니다."
  ],
  [
    "직업과 역할 내용 오른쪽1",
    "직업과 역할 내용 오른쪽2",
    "직업과 역할 내용 오른쪽3",
  ],
  ["게임 시스템 내용 오른쪽1", "게임 시스템 내용 오른쪽2"],
  ["게임 환경 내용 오른쪽1", "게임 환경 내용 오른쪽2"],
  ["게임 진행 내용 오른쪽1", "게임 진행 내용 오른쪽2"],
  ["부록 및 색인 내용 오른쪽1", "부록 및 색인 내용 오른쪽2"],
];

// 각 탭의 최대 페이지 수 계산
const maxPage = computed(() => {
  // 페이지 수는 각 탭의 내용 배열 길이에 따라 다름
  return Math.max(
    leftPages[selectedTab.value].length,
    rightPages[selectedTab.value].length
  );
});

const selectTab = (index) => {
  selectedTab.value = index;
  currentPage.value = 0;
};

const nextPage = () => {
  if (currentPage.value < maxPage.value - 1) {
    currentPage.value++;
  }
};

const prevPage = () => {
  if (currentPage.value > 0) {
    currentPage.value--;
  }
};

const modalStyle = computed(() => ({
  backgroundImage: `url(${rulebookImage})`,
  backgroundSize: "contain",
  backgroundPosition: "center",
  backgroundRepeat: "no-repeat",
}));

const contentStyle = computed(() => ({
  display: "flex",
  flexDirection: "column",
  justifyContent: "center",
  alignItems: "center",
  height: "100%",
  overflow: "hidden",
}));
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 오버레이가 맨 위에 오도록 설정 */
}

.rulebook-container {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  position: relative;
  width: 1050px; /* Set a fixed width */
  height: 740px; /* Set a fixed height based on your book image aspect ratio */
  max-width: 90vw; /* Limit maximum width for smaller screens */
  max-height: 90vh; /* Limit maximum height for smaller screens */
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-header h2 {
  margin: 0;
  font-size: 24px;
  color: #000;
}

.modal-body {
  flex-grow: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
}

.rulebook {
  display: flex;
  width: 100%;
  height: 100%;
  position: relative;
}

.tabs {
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  position: absolute;
  left: -140px; /* 탭 너비에 따라 조정 */
  top: 20%; /* 헤더 높이에 따라 조정 */
}

.tab {
  margin: 5px 0;
  cursor: pointer;
  position: relative;
  width: 100%;
  height: 20%;
}

.tab img {
  width: 100%;
  height: 100%;
}

.tab span {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-weight: bold;
  font-size: 14px;
  white-space: nowrap;
}

.page-content {
  flex: 1;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  padding: 20px;
  width: 100%;
  position: relative;
  height: 100%;
}

.page {
  width: 50%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  padding: 40px 30px 20px;
  box-sizing: border-box;
  overflow-y: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
  overflow: hidden;
}

.page:not(.table-of-contents) {
  padding-top: 40px;
}

.page.left {
  position: relative;
  padding-right: 20px;
  padding-left: 50px;
}

.page-text {
  max-width: 90%;
  max-height: 100%;
  font-size: 17px;
  line-height: 1.5;
  color: #000;
  margin: 10px 0;
  padding: 15px 15px;
}

.first-page-image {
  width: 80%; /* 이미지 크기 조정 */
  max-height: 80%; /* 최대 높이 설정 */
  object-fit: contain; /* 이미지 비율 유지 */
  margin: auto; /* 상하좌우 중앙 정렬 */
  display: block;
  position: absolute;
  top: 0%;
  bottom: 0;
  left: 0;
  right: 0;
}

.table-of-contents {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  width: 80%;
  margin: 0 auto;
}

.table-of-contents .page-text {
  text-align: center;
  font-size: 22px;
  line-height: 1.8;
  font-weight: bold;
}

.table-of-contents .page-text br {
  display: block;
  margin: 15px 0;
  content: "";
}

.page:not(.table-of-contents) .page-text {
  text-align: left;
  font-weight: normal;
}

/* 일반 페이지의 첫 번째 문단 (주로 제목) 스타일 */
/* .page:not(.table-of-contents) .page-text:first-child {
  text-align: center;
  margin-bottom: 20px;
  font-weight: bold;
} */

/* .toc-text {
  text-align: center;
  font-size: 22px;
  line-height: 1.8;
  font-weight: bold;
}

.toc-text br {
  display: block;
  margin: 15px 0;
  content: "";
} */

/* 네비게이션 버튼의 위치 고정 */
.nav-button {
  position: absolute;
  bottom: 5%;
  width: 40px;
  height: 40px;
  background-color: transparent;
  border: none;
  cursor: pointer;
  z-index: 10;
}

.nav-button.prev {
  left: 3%; /* Adjust this value as needed */
  bottom: 10%; /* Adjust this value as needed */
}

.nav-button.next {
  right: 5%; /* Adjust this value as needed */
  bottom: 10%; /* Adjust this value as needed */
}

/* .nav-button img {
  width: 100%;
  height: 100%;
  object-fit: contain;
} */

.modal-footer {
  margin-top: 20px;
}

button {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #444;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

/* 페이지 전환 애니메이션 */
/* .page-flip-enter-active,
.page-flip-leave-active {
  transition: transform 0.6s;
  transform-style: preserve-3d;
}

.page-flip-enter,
.page-flip-leave-to {
  transform: rotateY(-180deg);
} */

 /* ... (other styles remain the same) ... */

/* Updated navigation button styles */

</style>