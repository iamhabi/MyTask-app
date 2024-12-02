export type Task = {
  id: string
  parent_id: string | undefined
  title: string
  description: string | undefined
  is_done: boolean
  dueDate: Date | undefined
  created: Date
}