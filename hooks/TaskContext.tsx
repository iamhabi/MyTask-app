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
      id: uuidv4(),
      parent_id: parent_id,
      title: title,
      description: description,
      dueDate: dueDate
    }

    setTasks([...tasks, newTask])
  }

  const toggleTask = (id: string) => {
    console.log('toggle: ' + id)
  }

  const updateTask = (newTask: Task) => {
    const index = tasks.findIndex((task: Task) => {
      return task.id === newTask.id
    })

    tasks[index] = newTask
    
    setTasks([...tasks])
  }

  const deleteTask = (id: string) => {
    setTasks(tasks.filter(task => task.id !== id))
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