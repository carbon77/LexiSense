<script setup lang="ts">
import {ref} from "vue";

const items = ref()
</script>

<template>
  <Menubar class="flex-auto" :model="items">
    <template #start>
      <h1 class="text-2xl">LexiSense</h1>
    </template>
  </Menubar>
</template>