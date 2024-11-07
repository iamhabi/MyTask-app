import React, { useState } from 'react';
import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem from '@/components/TaskItem';
import { useAppNavigation } from './_layout';
import { Task } from "@/types/task";

export default function HomeScreen() {
  const navigation = useAppNavigation()
  
  const [tasks, updateTasks] = useState(Array<Task>)

  const deleteTask = (taskId: number) => {
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
              navigation.navigate('Detail', {
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
        onAdded={(task) => {
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