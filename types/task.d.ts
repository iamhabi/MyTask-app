export type Task = {
  uuid: string
  parent_uuid: string | undefined
  title: string
  description: string | undefined
  is_done: boolean
  dueDate: Date | undefined
  created: Date
}