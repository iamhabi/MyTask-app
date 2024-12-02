import { createContext, ReactNode, useContext, useState } from "react"
import AsyncStorage from '@react-native-async-storage/async-storage';
import { URLS } from "@/constants/urls"
import { Task } from "@/types/task";

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
    parent_id: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => void
  toggleTask: (
    id: string,
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
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
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
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
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
    let access = await AsyncStorage.getItem('access') ?? ""
    let user_id = await AsyncStorage.getItem('user_id') ?? ""

    await fetch(`${URLS.BASE_URL}${URLS.TASKS}`, {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer  ' + access,
        'user': user_id
      }
    })
    .then(response => response.json())
    .then(json => {
      setTasks(json as Task[])

      onSuccess()
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const addTask = async (
    parent_id: string | undefined,
    title: string,
    description: string | undefined,
    dueDate: Date | undefined,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {
    let access = await AsyncStorage.getItem('access') ?? ""
    let user_id = await AsyncStorage.getItem('user_id') ?? ""

    await fetch(`${URLS.BASE_URL}${URLS.TASKS}`, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        'Authorization': 'Bearer  ' + access,
        'user': user_id,
      },
      body: JSON.stringify({
        'parent_uuid': parent_id ?? "",
        'title': title,
        'description': description ?? "",
        'due_date': dueDate ?? "",
      })
    })
    .then(response => response.json())
    .then(json => {
      const newTask: Task = {
        id: json['pk'],
        parent_id: parent_id,
        title: title,
        description: description,
        is_done: false,
        dueDate: dueDate,
        created: json['created']
      }

      setTasks([...tasks, newTask])

      onSuccess()
    })
    .catch(error => {
      onFailed(error)
    })
  }

  const toggleTask = async (
    id: string,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {

  }

  const updateTask = async (
    newTask: Task,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {

  }

  const deleteTask = async (
    id: string,
    onSuccess: () => void,
    onFailed: (
      error: string
    ) => void
  ) => {
    let access = await AsyncStorage.getItem('access') ?? ""
    let user_id = await AsyncStorage.getItem('user_id') ?? ""

    fetch(`${URLS.BASE_URL}${URLS.TASKS}${user_id}/`, {
      method: 'DELETE',
      headers: {
        'Accept': '*/*',
        'Content-Type': 'text/plain',
        'Authorization': 'Bearer  ' + access,
        'user': user_id,
      }
    })
    .then(response => response.json)
    .then(json => {
      console.log(json)

      // setTasks(tasks.filter(task => task.id !== id))

      onSuccess()
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
    toggleTask,
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