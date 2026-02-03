<script setup lang="ts">
import AudioButton from "@/components/AudioButton.vue";

const {word, definitions} = defineProps(['word', 'definitions'])
</script>

<template>
  <Panel :header="word" toggleable collapsed>
    <template v-if="definitions.length > 0" v-for="definition in definitions">
      <div class="flex items-baseline justify-between">
        <div class="flex items-baseline gap-2">
          <span class="font-bold text-lg">{{ definition.word }}</span>
          <span v-if="definition.phonetic" class="font-small text-sm">{{ definition.phonetic }}</span>
        </div>
        <Button
            as="a"
            v-if="definition.sourceUrls"
            label="Source"
            :href="definition.sourceUrls[0]"
            variant="link"
            target="_blank"
        />
      </div>
      <div class="ml-5">
        <span v-if="definition.meanings">Meanings</span>
        <div v-if="definition.meanings" class="flex flex-col gap-3 mb-5">
          <div v-for="meaning in definition.meanings">
            <span class="text-gray-400 font-light italic text-lg">{{ meaning.partOfSpeech }}</span>
            <div v-if="meaning.definitions" class="flex flex-col gap-1">
              <div v-for="({definition}, index) in meaning.definitions.slice(0, 5)">
                <span>{{ index + 1 }}. {{ definition }}</span>
              </div>
            </div>
          </div>
        </div>

        <span v-if="definition.phonetics">Phonetics</span>
        <div v-if="definition.phonetics" class="flex flex-col gap-1">
          <div v-for="(phonetic, index) in definition.phonetics.filter(phonetic => !!phonetic.text)">
            <div class="flex items-baseline gap-3">
              <span>{{ index + 1 }}. {{ phonetic.text }}</span>
              <AudioButton v-if="phonetic.audio" :audioUrl="phonetic.audio"/>
            </div>
          </div>
        </div>
      </div>
    </template>
    <template v-else>
      <span>Sorry! We can't find any definitions for this word.</span>
    </template>
  </Panel>
</template>