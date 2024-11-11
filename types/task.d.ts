export type Task = {
  id: string
  parent_id: string | undefined
  title: string
  description: string | undefined
  dueDate: Date | undefined
}