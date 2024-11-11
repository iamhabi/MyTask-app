import { Task } from "@/types/task";
import 'react-native-get-random-values';
import { v4 as uuidv4 } from 'uuid';

export const createTask = (parent_id: number | undefined, title: string, description: string | undefined, dueDate: Date | undefined): Task => ({
  id: uuidv4(),
  parent_id: parent_id,
  title: title,
  description: description,
  dueDate: dueDate
})