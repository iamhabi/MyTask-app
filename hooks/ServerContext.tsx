import { URLS } from "@/constants/urls"
import { createContext, ReactNode, useContext } from "react"

export interface ServerContextType {
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
}

const ServerContext = createContext<ServerContextType | undefined>(undefined)

interface ServerProviderProps {
  children: ReactNode
}

export function ServerProvider({ children }: ServerProviderProps) {
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
        Accept: 'application/json',
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
        Accept: 'application/json',
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

  const value: ServerContextType = {
    register,
    login
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