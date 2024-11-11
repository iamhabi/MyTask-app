import { useState } from "react";
import { FlatList, StyleSheet, Text, View } from "react-native";
import { useAppNavigation } from '@/types/navigation';

import TaskInputField from '@/components/TaskInputField';
import TaskItem from "@/components/TaskItem";

import { Task } from "@/types/task";
import { ROUTES } from "@/constants/routes";
import { useTaskContext } from "@/hooks/TaskContext";

export type Props = {
  parent_id: string | undefined
}

export default function SubTasksScreen({ parent_id }: Props) {
  const navigation = useAppNavigation()

  const { tasks, addTask, deleteTask } = useTaskContext()

  const subTasks = tasks.filter(task => task.parent_id === parent_id)

  const isEmpty = subTasks.length === 0

  return (
    <View style={styles.container}>
      {
        !isEmpty ? (
          <FlatList
            style={styles.listContainer}
            data={subTasks}
            renderItem={(item) => 
              <TaskItem
                task={item.item}
                onClick={(task) => {
                  navigation.push(ROUTES.DETAIL, {
                    taskJSON: JSON.stringify(task)
                  })
                }}
                onDelete={(deletedTask: Task) => {
                  deleteTask(deletedTask.id)
                }}
              />
            }
          />
        ) : (
          <View style={styles.listContainer}>
            <Text>
              No subtasks
            </Text>
          </View>
        )
      }
      
      <TaskInputField
        onCreate={(title, description, dueDate) => {
          addTask(parent_id, title, description, dueDate)
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
  },
  listContainer: {
    flex: 1,
    marginStart: 16,
  }
});