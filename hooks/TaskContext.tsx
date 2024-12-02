import { Task } from "@/types/task";
import { createContext, ReactNode, useContext, useState } from "react";

import 'react-native-get-random-values';
import { v4 as uuidv4 } from 'uuid';

export interface TaskContextType {
  tasks: Task[]
  addTask: (
    parent_id: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined
  ) => void
  toggleTask: (id: string) => void
  updateTask: (newTask: Task) => void
  deleteTask: (id: string) => void
}

const TaskContext = createContext<TaskContextType | undefined>(undefined)

interface TaskProviderProps {
  children: ReactNode
}

export function TaskProvider({ children }: TaskProviderProps) {
  const [tasks, setTasks] = useState<Task[]>([])

  const addTask = (
    parent_id: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined
  ) => {
    const newTask: Task = {
      uuid: uuidv4(),
      parent_uuid: parent_id,
      title: title,
      description: description,
      is_done: false,
      dueDate: dueDate,
      created: new Date()
    }

    setTasks([...tasks, newTask])
  }

  const toggleTask = (id: string) => {
    setTasks(tasks.map(task =>
      task.uuid === id ? { ...task, is_done: !task.is_done } : task
    ))
  }

  const updateTask = (newTask: Task) => {
    setTasks(tasks.map(task =>
      task.uuid === newTask.uuid ? newTask : task
    ))
  }

  const deleteTask = (id: string) => {
    setTasks(tasks.filter(task => task.uuid !== id))
  }

  const value: TaskContextType = {
    tasks,
    addTask,
    toggleTask,
    updateTask,
    deleteTask
  }

  return (
    <TaskContext.Provider value={value}>
      {children}
    </TaskContext.Provider>
  )
}

export function useTaskContext(): TaskContextType {
  const context = useContext(TaskContext)

  if (context === undefined) {
    throw new Error('useTaskContext must be used within a TaskProvider')
  }

  return context
}