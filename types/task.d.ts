export type Task = {
  id: string
  parent_id: number | undefined
  title: string
  description: string | undefined
  dueDate: Date | undefined
}