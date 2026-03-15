<script setup lang="ts">
import { ref, computed } from "vue";
import { useColumns } from "./columns";
import Empty from "./empty.svg?component";

const props = defineProps({
  tableData: {
    type: Array as () => any[],
    default: () => []
  }
});

const { loading, columns, pagination } = useColumns(props);

const currentPage = ref(1);
const pagedData = computed(() => {
  const start = (currentPage.value - 1) * pagination.pageSize;
  return props.tableData.slice(start, start + pagination.pageSize);
});

function onCurrentChange(page: number) {
  currentPage.value = page;
}
</script>

<template>
  <pure-table
    row-key="id"
    alignWhole="center"
    showOverflowTooltip
    :loading="loading"
    :loading-config="{ background: 'transparent' }"
    :data="pagedData"
    :columns="columns"
    :pagination="{ ...pagination, total: tableData.length, currentPage }"
    @page-current-change="onCurrentChange"
  >
    <template #empty>
      <el-empty description="暂无课程数据" :image-size="60">
        <template #image>
          <Empty />
        </template>
      </el-empty>
    </template>
  </pure-table>
</template>

<style lang="scss">
.pure-table-filter {
  .el-table-filter__list {
    min-width: 80px;
    padding: 0;

    li {
      line-height: 28px;
    }
  }
}
</style>

<style lang="scss" scoped>
:deep(.el-table) {
  --el-table-border: none;
  --el-table-border-color: transparent;

  .el-empty__description {
    margin: 0;
  }

  .el-scrollbar__bar {
    display: none;
  }
}
</style>
