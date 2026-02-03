<script setup lang="ts">
import HeaderMenu from "@/components/HeaderMenu.vue";
</script>

<template>
  <div class="p-3 flex flex-col gap-2">
    <HeaderMenu />
    <RouterView />
  </div>
</template>
