import { ref, type PropType } from "vue";
import type { PaginationProps } from "@pureadmin/table";
import Empty from "./empty.svg?component";

export function useColumns(props: { tableData: any[] }) {
  const loading = ref(false);
  const columns: TableColumnList = [
    {
      sortable: true,
      label: "课程名称",
      prop: "courseName",
      minWidth: 120
    },
    {
      sortable: true,
      label: "授课教师",
      prop: "teacherName",
      minWidth: 80
    },
    {
      sortable: true,
      label: "学生人数",
      prop: "studentCount",
      minWidth: 80
    },
    {
      sortable: true,
      label: "课程状态",
      prop: "status",
      minWidth: 80,
      cellRenderer: ({ row }) => {
        const statusMap: Record<number, { text: string; type: string }> = {
          0: { text: "已结课", type: "info" },
          1: { text: "进行中", type: "success" },
          2: { text: "未开始", type: "warning" }
        };
        const s = statusMap[row.status] || { text: "未知", type: "info" };
        return <el-tag type={s.type} size="small">{s.text}</el-tag>;
      }
    },
    {
      sortable: true,
      label: "创建时间",
      prop: "createTime",
      minWidth: 130
    }
  ];

  /** 分页配置 */
  const pagination = {
    pageSize: 10,
    currentPage: 1,
    layout: "prev, pager, next",
    total: 0,
    align: "center" as const
  };

  return {
    Empty,
    loading,
    columns,
    pagination
  };
}
