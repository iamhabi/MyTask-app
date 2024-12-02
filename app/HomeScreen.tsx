import { FlatList, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import TaskInputField from '@/components/TaskInputField';
import TaskItem from '@/components/TaskItem';
import { useAppNavigation } from '@/types/navigation';
import { ROUTES } from '@/constants/routes';
import { useServerContext } from '@/hooks/ServerContext';
import { useEffect, useState } from 'react';

export default function HomeScreen() {
  const navigation = useAppNavigation()
  
  const [isUp, setUp] = useState(false)
  const { tasks, getTasks, addTask, deleteTask } = useServerContext()

  useEffect(() => {
    if (!isUp) {
      getTasks(
        () => {
          
        },
        (error) => {
          console.log(error)
        }
      )

      setUp(true)
    }
  })

  return (
    <SafeAreaView style={styles.container}>
      <FlatList
        style={{ flex: 1 }}
        data={
          tasks.filter(task => task.parent_id === undefined)
        }
        renderItem={(item) => 
          <TaskItem
            task={item.item}
            onClick={(task) => {
              navigation.navigate(ROUTES.DETAIL, {
                taskJSON: JSON.stringify(task)
              })
            }}
            onDelete={(deletedTask) => {
              deleteTask(
                deletedTask.id,
                () => {
                  // Task deleted
                },
                (error) => {

                }
              )
            }}
          />
        }
      />
      <TaskInputField
        onCreate={(title, description, dueDate) => {
          addTask(
            undefined, title, description, dueDate,
            () => {
              // Task added
            },
            (error) => {
              console.error(error)
            }
          )
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