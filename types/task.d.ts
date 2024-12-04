export type Task = {
  id: string
  parentId: string | undefined
  title: string
  description: string | undefined
  isDone: boolean
  dueDate: Date | undefined
  created: Date
}