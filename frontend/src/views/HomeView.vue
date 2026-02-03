<script setup lang="ts">
import {ref} from "vue";
import {api} from "@/api.ts";
import WordDefinition from "@/components/WordDefinition.vue";

const sentence = ref("")
const definitions = ref()
const loading = ref(false)

async function analyze() {
  if (sentence.value === "") return
  loading.value = true

  const response = await api.get("/analyze/sentence", {
    params: {
      sentence: sentence.value,
    }
  })
  const words = sentence.value
      .replace(/[^a-zA-Z0-9\s]/g, "")
      .split(/\s+/)
      .filter(word => word.length > 0)
  definitions.value = words.map(word => [word, response.data[word.toLowerCase()]])
  loading.value = false
}
</script>

<template>
  <div class="w-full mt-5 flex flex-col items-center gap-2">
    <h1 class="text-3xl font-bold">Sentence analyzer</h1>
    <div class="w-1/2 flex gap-2 justify-around">
      <InputText v-model="sentence" class="flex-auto" type="text" size="large"/>
      <Button label="Analyze" size="large" @click="analyze"/>
    </div>
    <div class="w-1/2 mt-5 flex flex-col gap-2">
      <template v-if="loading">
        <p class="text-lg">Loading...</p>
      </template>
      <template v-else-if="!!definitions" v-for="[word, definition] in definitions">
        <WordDefinition :word="word" :definitions="definition" />
      </template>
    </div>
  </div>
</template>
