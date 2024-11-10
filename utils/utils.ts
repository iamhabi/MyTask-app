import { Task } from "@/types/task"

export const createTask = (parent_id: number | undefined, title: string, description: string | undefined, dueDate: Date | undefined): Task => ({
  id: new Date().getTime(),
  parent_id: parent_id,
  title: title,
  description: description,
  dueDate: dueDate
})