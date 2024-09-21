<template>
  <div  class="no-padding">
    <div class="carousel-inner">
      <div class="carousel-item active">
        <img src="@/assets/images/carousel/slide7.gif" 
        class="d-block" alt="" />
        <div class="carousel-caption" @click="navigateToLobby">
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const navigateToLobby = () => {
  router.push({ name: 'Lobby' });
};

</script>

<style scoped>
.no-padding {
  padding: 0 !important;
  margin: 0 !important;
}

.carousel-indicators{
  margin-bottom: 0px;
  transform: translateY(-90%); 
}

.carousel-indicators button {
  width: 110px; /* Adjust width */
  height: 12px; /* Adjust height */
  /* border-radius: 11px !important; */
  margin-right: 1%;
}

.carousel-item {
  height: 850px;
  /* background-color: #ffffff; */
  background: linear-gradient(270deg, rgba(26, 26, 26, 0.45) 65%, #0a0a10 100%), linear-gradient(89.84deg, rgba(60, 60, 60, 0.9) 65.72%, #0a0a10 100%);
}

.carousel-item img {
  /* object-fit: cover; */
  object-fit: fill;
  height: 100%;
  width: 100%;
  filter: drop-shadow(0px 4px 4px rgba(0, 0, 0, 0.25));
}

.carousel-caption {
  padding: 10px;
  border-radius: 10px;
  max-width: 400px;
  margin: 0 auto;
  transition: transform 0.3s ease-in-out;
  cursor: pointer;
}

.carousel-caption:hover {
  transform: scale(1.05);
}

.carousel-caption h5 {
  color: #ffffff;
  font-size: 1.2rem;
  margin-bottom: 10px;
}

.carousel-caption .btn {
  color: #ffffff;
  text-decoration: none;
}
</style>