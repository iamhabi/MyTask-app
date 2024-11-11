export type Task = {
  id: string
  parent_id: string | undefined
  title: string
  description: string | undefined
  completed: boolean
  dueDate: Date | undefined
}