export interface Task {
  id: number
  parent_id: number | undefined
  title: string
  description: string | undefined
  dueDate: Date | undefined
}

