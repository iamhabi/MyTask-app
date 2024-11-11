import React, { useState } from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem from '@/components/TaskItem';
import { useAppNavigation } from '@/types/navigation';
import {  Task } from "@/types/task";
import { ROUTES } from '@/constants/routes';
import { createTask } from '@/utils/utils';

export default function HomeScreen() {
  const navigation = useAppNavigation()
  
  const [tasks, updateTasks] = useState(Array<Task>)

  const deleteTask = (taskId: string) => {
    updateTasks(
      tasks.filter(task =>
        task.id !== taskId
      )
    )
  }

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
        data={tasks}
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onClick={(task) => {
              navigation.navigate(ROUTES.DETAIL, {
                taskJSON: JSON.stringify(task)
              })
            }}
            onDelete={(deletedTask) => {
              deleteTask(deletedTask.id)
            }}
          />
        }
      />
      <TaskInputField
        onCreate={(title, description, dueDate) => {
          const task = createTask(undefined, title, description, dueDate)

          updateTasks([...tasks, task])
        }}
      />
    </SafeAreaView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
})