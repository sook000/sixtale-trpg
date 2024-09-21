<template>
  <section class="popular-scenario">
    <div class="container">
      <div class="header">
        <h1>인기 시나리오</h1>
      </div>
      <div class="w-100 mx-auto mt-4">
        <Carousel id="activeClasses" :itemsToShow="2.2" :wrapAround="true" :transition="500" :autoplay="3000">
              <Slide class="scenario-card" v-for="scenario in scenarios" :key="scenario.title">
                  <div class="card">
                    <img :src="require(`@/assets/images/${scenario.image}`)" :alt="scenario.title" class="static-image" />
                    <img :src="require(`@/assets/images/popular/${scenario.gif}`)" :alt="scenario.title" class="gif-image" />
                  </div>
              </Slide>
              <template #addons>
                  <Navigation />
              </template>
          </Carousel>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from "vue";
import { Carousel, Pagination, Slide, Navigation } from "vue3-carousel";

const scenarios = ref([
  {
    image: 'scenario.png',
    gif: 'card1.gif',
    title: 'Scenario 1',
    genre: 'Genre 1'
  },
  {
    image: "scenario.png",
    gif: "card2.gif",
    title: "Scenario 2",
    genre: "Genre 2",
  },
  {
    image: "scenario_img.png",
    gif: "card3.gif",
    title: "Scenario 3",
    genre: "Genre 3",
  },
  {
    image: "scenario_img.png",
    gif: "card4.gif",
    title: "Scenario 4",
    genre: "Genre 4",
  },
  {
    image: "scenario_img.png",
    gif: "card5.gif",
    title: "Scenario 5",
    genre: "Genre 5",
  },
]);
</script>


<style scoped>
.popular-scenario {
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%),
    linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
  color: white;
  padding: 0;
}

.container {
  padding: 200px 20px !important;
  border-radius: 0;
  max-width: 1200px;
  margin: 0 auto;
  text-align: center;
}

.header {
  margin-top: 20px;
  margin-bottom: 50px;
}

.static-image,
.gif-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s;
}



.scenario-card:hover {
  transform: scale(1.05);
}

.card {
  min-width: 500px;
  min-height: 300px;
}

.gif-image {
  opacity: 0;
}

.scenario-card:hover .gif-image {
  opacity: 1;
}

.scenario-card:hover .static-image {
  opacity: 0;
}



#activeClasses {
  .carousel__slide {
    padding: 50px;
    transition: transform 0.5s, opacity 0.5s;
  }

  .carousel__viewport {
    perspective: 1000px;
  }

  .card {
    min-width: 500px;
    min-height: 300px;
    background-color: black;
  }

  .carousel__track {
    transform-style: preserve-3d;
  }



  .carousel__slide--sliding {
    transition: 0.5s;
  }

  .carousel__slide {
    opacity: 0.6;
    transform: rotateY(-15deg) scale(0.8);
  }

  .carousel__slide--active {
    opacity: 1;
    transform: rotateY(0) scale(1.2);
  }

  .carousel__slide--prev {
    opacity: 0.3;
    transform: rotateY(-10deg) scale(0.8);
  }

  .carousel__slide--next {
    opacity: 0.3;
    transform: rotateY(10deg) scale(0.8);
  }
}
:root {
  --vc-clr-primary: #000;
  --vc-clr-secondary: #090f207f;
  --vc-clr-white: #ffffff;

  --vc-icn-width: 1.2em;

  --vc-nav-width: 30px;
  --vc-nav-height: 30px;
  --vc-nav-border-radius: 0;
  --vc-nav-color: var(--vc-clr-primary);
  --vc-nav-color-hover: var(--vc-clr-secondary);
  --vc-nav-background: transparent;

  --vc-pgn-width: 12px;
  --vc-pgn-height: 4px;
  --vc-pgn-margin: 4px;
  --vc-pgn-border-radius: 0;
  --vc-pgn-background-color: var(--vc-clr-secondary);
  --vc-pgn-active-color: var(--vc-clr-primary);
}

.carousel {
  position: relative;
  text-align: center;
  box-sizing: border-box;
  touch-action: pan-y;
  overscroll-behavior: none;
}

.carousel.is-dragging {
  touch-action: none;
}

.carousel * {
  box-sizing: border-box;
}



.carousel__viewport {
  overflow: hidden;
}

.carousel__sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  border: 0;
}

.carousel__icon {
  width: var(--vc-icn-width);
  height: var(--vc-icn-width);
  fill: currentColor;
}

.carousel__prev,
.carousel__next {
  box-sizing: content-box;
  background: var(--vc-nav-background);
  border-radius: var(--vc-nav-border-radius);
  width: var(--vc-nav-width);
  height: var(--vc-nav-height);
  text-align: center;
  font-size: var(--vc-nav-height);
  padding: 0;
  color: var(--vc-nav-color);
  display: flex;
  justify-content: center;
  align-items: center;
  position: absolute;
  border: 0;
  cursor: pointer;
  margin: 0 10px;
  top: 50%;
  transform: translateY(-50%);
}

.carousel__next--disabled,
.carousel__prev--disabled {
  cursor: not-allowed;
  opacity: 0.5;
}

.carousel__prev {
  left: 0;
}

.carousel__next {
  right: 0;
}

.carousel--rtl .carousel__prev {
  left: auto;
  right: 0;
}

.carousel--rtl .carousel__next {
  right: auto;
  left: 0;
}

@media (hover: hover) {
  .carousel__prev:hover,
  .carousel__next:hover {
    color: var(--vc-nav-color-hover);
  }
}

.carousel__pagination {
  display: flex;
  justify-content: center;
  list-style: none;
  line-height: 0;
  margin: 10px 0 0;
  padding: 0;
}

.carousel__pagination-button {
  display: block;
  border: 0;
  margin: 0;
  cursor: pointer;
  padding: var(--vc-pgn-margin);
  background: transparent;
}

.carousel__pagination-button::after {
  display: block;
  content: "";
  width: var(--vc-pgn-width);
  height: var(--vc-pgn-height);
  border-radius: var(--vc-pgn-border-radius);
  background-color: var(--vc-pgn-background-color);
}

.carousel__pagination-button--active::after {
  background-color: var(--vc-pgn-active-color);
}

@media (hover: hover) {
  .carousel__pagination-button:hover::after {
    background-color: var(--vc-pgn-active-color);
  }
}

.carousel__slide {
  scroll-snap-stop: auto;
  flex-shrink: 0;
  margin: 0;
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  transform: translateZ(0); /* Fix iOS scrolling #22 */
}
</style>
<!-- <template>
  <section class="popular-scenario">
    <div class="container">
      
      <div class="header">
        <h3>인기 시나리오</h3>
      </div>
      <div class="scenarios-grid">
        <div class="scenario-card" v-for="(scenario, index) in scenarios" :key="index">
          <div class="image-container">
            <img :src="require(`@/assets/images/popular/${scenario.image}`)" :alt="scenario.title" class="static-image" />
            <img :src="require(`@/assets/images/popular/${scenario.gif}`)" :alt="scenario.title" class="gif-image" />
          </div>
          <div class="info">
            <h3>{{ scenario.title }}</h3>
            <p>{{ scenario.genre }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue';

const scenarios = ref([
  {
    image: 'card1.jpg',
    gif: 'card1.gif',
    title: 'Scenario 1',
    genre: 'Genre 1'
  },
  {
    image: 'card2.jpg',
    gif: 'card2.gif',
    title: 'Scenario 2',
    genre: 'Genre 2'
  },
  {
    image: 'card3.jpg',
    gif: 'card3.gif',
    title: 'Scenario 3',
    genre: 'Genre 3'
  },
  {
    image: 'card4.jpg',
    gif: 'card4.gif',
    title: 'Scenario 4',
    genre: 'Genre 4'
  },
  {
    image: 'card5.jpg',
    gif: 'card5.gif',
    title: 'Scenario 5',
    genre: 'Genre 5'
  }
]);
</script>

<style scoped>
.popular-scenario {
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%), linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
  
  color: white;
  padding:2%; 
}

.container {
  padding: 40px 20px; 
  border-radius: 0; 
  max-width: 1200px; 
  margin: 0 auto;
  text-align: center;
}

.header {
  margin-top: 20px;
  margin-bottom: 20px;
  display: flex;
}

.header h2 {
  font-size: 2.5rem; 
  margin: 0;
}

.scenarios-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
}

.scenario-card {
  padding: 20px;
  border-radius: 10px;
  overflow: hidden;
  transition: transform 0.3s;
  text-align: center;
}

.scenario-card:hover {
  transform: scale(1.05);
}

.image-container {
  position: relative;
  width: 100%;
  padding-top: 75%; 
  overflow: hidden;
}

.static-image,
.gif-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: opacity 0.3s;
}

.gif-image {
  opacity: 0;
}

.scenario-card:hover .gif-image {
  opacity: 1;
}

.scenario-card:hover .static-image {
  opacity: 0;
}

.info {
  padding: 10px;
  text-align: center;
}

.info h3 {
  margin-bottom: 10px;
  font-size: 1.5rem; 
}

.info p {
  margin: 0;
}
</style> -->
