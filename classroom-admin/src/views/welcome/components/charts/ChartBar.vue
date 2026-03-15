<script setup lang="ts">
import { useDark, useECharts } from "@pureadmin/utils";
import { type PropType, ref, computed, watch, nextTick } from "vue";

const props = defineProps({
  courseNames: {
    type: Array as PropType<Array<string>>,
    default: () => []
  },
  studentData: {
    type: Array as PropType<Array<number>>,
    default: () => []
  },
  assignmentData: {
    type: Array as PropType<Array<number>>,
    default: () => []
  }
});

const { isDark } = useDark();

const theme = computed(() => (isDark.value ? "dark" : "light"));

const chartRef = ref();
const { setOptions } = useECharts(chartRef, {
  theme
});

watch(
  () => props,
  async () => {
    await nextTick();
    setOptions({
      container: ".bar-card",
      color: ["#41b6ff", "#26ce83"],
      tooltip: {
        trigger: "axis",
        axisPointer: {
          type: "none"
        }
      },
      grid: {
        top: "20px",
        left: "50px",
        right: 0,
        bottom: "40px"
      },
      legend: {
        data: ["学生人数", "作业数量"],
        textStyle: {
          color: "#606266",
          fontSize: "0.875rem"
        },
        bottom: 0
      },
      xAxis: [
        {
          type: "category",
          data: props.courseNames,
          axisLabel: {
            fontSize: "0.75rem",
            rotate: props.courseNames.length > 5 ? 20 : 0,
            overflow: "truncate",
            width: 60
          },
          axisPointer: {
            type: "shadow"
          }
        }
      ],
      yAxis: [
        {
          type: "value",
          axisLabel: {
            fontSize: "0.875rem"
          },
          splitLine: {
            show: false
          }
        }
      ],
      series: [
        {
          name: "学生人数",
          type: "bar",
          barWidth: 10,
          itemStyle: {
            color: "#41b6ff",
            borderRadius: [10, 10, 0, 0]
          },
          data: props.studentData
        },
        {
          name: "作业数量",
          type: "bar",
          barWidth: 10,
          itemStyle: {
            color: "#26ce83",
            borderRadius: [10, 10, 0, 0]
          },
          data: props.assignmentData
        }
      ]
    });
  },
  {
    deep: true,
    immediate: true
  }
);
</script>

<template>
  <div ref="chartRef" style="width: 100%; height: 365px" />
</template>
