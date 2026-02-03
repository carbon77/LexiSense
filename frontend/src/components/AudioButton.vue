<script setup lang="ts">
import {ref} from "vue";

const {audioUrl} = defineProps(["audioUrl"])

const audio = ref<HTMLAudioElement | null>(null)

function play() {
  audio.value?.play().catch(console.error)
}
</script>

<template>
<Button
    icon="pi pi-play"
    variant="text"
    rounded
    size="small"
    @click="play"
/>
  <audio ref="audio" :src="audioUrl"/>
</template>