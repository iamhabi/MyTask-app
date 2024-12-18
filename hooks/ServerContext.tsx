import { createContext, ReactNode, useContext, useState } from "react"
import AsyncStorage from '@react-native-async-storage/async-storage';
import { URLS } from "@/constants/urls"
import { Task } from "@/types/task";
import HttpStatusCode from "@/constants/HttpStatusCode";

export interface ServerContextType {
  tasks: Task[]
  register: (
    uesrname: string,
    email: string,
    password1: string,
    password2: string,
    onSuccess: () => void,
    onFailed: (
      error: JSON
    ) => void,
  ) => void
  login: (
    uesrname: string,
    password: string,
    onSuccess: (
      access: string,
      refresh: string,
      user_id: string,
    ) => void,
    onFailed: (
      error: JSON
    ) => void,
  ) => void
  getTasks:(
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void,
  ) => void
  addTask: (
    parentId: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => void
  updateTask: (
    newTask: Task,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => void
  deleteTask: (
    id: string,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => void
}

const ServerContext = createContext<ServerContextType | undefined>(undefined)

interface ServerProviderProps {
  children: ReactNode
}

export function ServerProvider({ children }: ServerProviderProps) {
  const [tasks, setTasks] = useState<Task[]>([])

  const register = async (
    uesrname: string,
    email: string,
    password1: string,
    password2: string,
    onSuccess: () => void,
    onFailed: (
      error: JSON
    ) => void,
  ) => {
    await fetch(URLS.BASE_URL + URLS.REGISTER, {
      method: 'POST',
      headers: getHeader(),
      body: JSON.stringify({
        username: uesrname,
        email: email,
        password1: password1,
        password2: password2
      })
    })
    .then(response => response.json())
    .then(json => {
      if (json.hasOwnProperty('username')) {
        onSuccess()
      } else {
        onFailed(json)
      }
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const login = async (
    username: string,
    password: string,
    onSuccess: (
      access: string,
      refresh: string,
      user_id: string,
    ) => void,
    onFailed: (
      error: JSON
    ) => void,
  ) => {
    await fetch(URLS.BASE_URL + URLS.TOKEN, {
      method: 'POST',
      headers: getHeader(),
      body: JSON.stringify({
        username: username,
        password: password,
      })
    })
    .then(response => response.json())
    .then(json => {
      if (json.hasOwnProperty('access')) {
        const access = json.access
        const refresh = json.refresh
        const user_id = json.user_id

        onSuccess(access, refresh, user_id)
      } else {
        onFailed(json)
      }
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const getTasks = async (
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void,
  ) => {
    await fetch(`${URLS.BASE_URL}${URLS.TASKS}`, {
      method: 'GET',
      headers: await getHeaderWithToken()
    })
    .then(response => response.json())
    .then(json => {
      if (json['response'] === HttpStatusCode.OK) {
        let tasksJson = json['tasks']

        for (let i in tasksJson) {
          let taskJson = tasksJson[i]

          const task: Task = {
            id: taskJson['id'],
            parentId: taskJson['parent_id'] ?? undefined,
            title: taskJson['title'],
            description: taskJson['description'] ?? undefined,
            isDone: taskJson['is_done'],
            dueDate: taskJson['due_date'] ?? undefined,
            created: taskJson['created']
          }

          setTasks([...tasks, task])
        }

        // setTasks(json['tasks'] as Task[])

        onSuccess()
      } else {
        onFailed('Failed to get tasks')
      }
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const addTask = async (
    parentId: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {
    await fetch(`${URLS.BASE_URL}${URLS.TASKS}`, {
      method: 'POST',
      headers: await getHeaderWithToken(),
      body: JSON.stringify({
        'parent_id': parentId ?? null,
        'title': title,
        'description': description ?? null,
        'due_date': dueDate ?? null,
      })
    })
    .then(response => response.json())
    .then(json => {
      if (json['response'] === HttpStatusCode.CREATED) {
        const newTask: Task = createTaskFromJson(json['task'])

        setTasks([...tasks, newTask])
  
        onSuccess()
      } else {
        onFailed('Failed to create task')
      }
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const updateTask = async (
    task: Task,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {
    await fetch(`${URLS.BASE_URL}${URLS.TASKS}${task.id}/`, {
      method: 'PUT',
      headers: await getHeaderWithToken(),
      body: JSON.stringify(task)
    })
    .then(response => response.json())
    .then(json => {
      if (json['response'] === HttpStatusCode.OK) {
        const updatedTask: Task = createTaskFromJson(json['task'])

        setTasks(tasks.map(task => task.id === updatedTask.id ? updatedTask : task))

        onSuccess()
      } else {
        onFailed('Failed to update task')
      }
    })
    .catch((error) => {
      onFailed(error)
    })
  }

  const deleteTask = async (
    id: string,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {
    fetch(`${URLS.BASE_URL}${URLS.TASKS}${id}/`, {
      method: 'DELETE',
      headers: await getHeaderWithToken()
    })
    .then(response => response.json())
    .then(json => {
      if (json['response'] == HttpStatusCode.OK) {
        setTasks(tasks.filter(task => task.id !== id))

        onSuccess()
      } else {
        onFailed('Failed to delete task')
      }
    })
    .catch((error) => {
      onFailed(error)
    })
  }

  const value: ServerContextType = {
    tasks,
    register,
    login,
    getTasks,
    addTask,
    updateTask,
    deleteTask
  }

  return (
    <ServerContext.Provider value={value}>
      {children}
    </ServerContext.Provider>
  )
}

export function useServerContext(): ServerContextType {
  const context = useContext(ServerContext)

  if (context === undefined) {
    throw new Error('useServerContext must be used within a TaskProvider')
  }

  return context
}

function getHeader(): any {
  return {
    'Accept': 'application/json',
    'Content-Type': 'application/json',
  }
}

async function getHeaderWithToken(): Promise<any> {
  let header = getHeader()

  let access = await AsyncStorage.getItem('access') ?? ""
  let user_id = await AsyncStorage.getItem('user_id') ?? ""

  header['Authorization'] = `Bearer  ${access}`
  header['user'] = user_id

  return header
}

function createTaskFromJson(json: any): Task {
  return {
    id: json['id'],
    parentId: json['parent_id'] ?? undefined,
    title: json['title'],
    description: json['description'] ?? undefined,
    isDone: json['is_done'],
    dueDate: json['due_date'] ?? undefined,
    created: json['created']
  }
}