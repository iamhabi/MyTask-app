import React, { useEffect, useState } from 'react';
import { useNavigation, useRoute } from "@react-navigation/native";
import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem, { Task } from '@/components/TaskItem';

export default function HomeScreen() {
  const [tasks, updateTasks] = useState(Array<Task>)

  const deleteTask = (taskId: number) => {
    updateTasks(
      tasks.filter(task =>
        task.id !== taskId
      )
    );
  };

  const navigation = useNavigation();
  const params = useRoute().params || {};

  useEffect(() => {
    const { updateTaskJSON = undefined, deletedTaskId = undefined } = params;

    if (updateTaskJSON) {
      const updatedTask = JSON.parse(updateTaskJSON) as Task;

      const dueDate = updatedTask.dueDate !== undefined ? new Date(updatedTask.dueDate) : undefined;

      const index = tasks.findIndex((task: Task) => {
        return task.id === updatedTask.id;
      });

      tasks[index] = updatedTask;
      tasks[index].dueDate = dueDate;

      navigation.setParams({
        updateTaskJSON: undefined
      });
    }
    
    if (deletedTaskId) {
      deleteTask(deletedTaskId);

      navigation.setParams({
        deletedTaskId: undefined
      });
    }
  }, [params]);

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
        data={tasks}
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onDelete={(deletedTask) => {
              deleteTask(deletedTask.id)
            }}
          />
        }
      />
      <TaskInputField
        onAdded={(task) => {
          updateTasks([...tasks, task]);
        }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    margin: 8,
  },
})