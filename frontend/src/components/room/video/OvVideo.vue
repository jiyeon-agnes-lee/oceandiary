<template>
  <video autoplay class="video-size" />
</template>

<script>
export default {
  name: "OvVideo",

  props: {
    streamManager: Object,
  },

  mounted() {
    this.streamManager.addVideoElement(this.$el);
  },
};
</script>

<style scoped>
.video-size {
  width: 100%;
  height: auto;
  border-radius: 15px;
  /* background-color: black; */
}
</style>
