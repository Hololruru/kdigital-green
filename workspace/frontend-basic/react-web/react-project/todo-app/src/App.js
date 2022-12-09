import { useState, useCallback } from 'react';
import axios from "axios";

import './App.css';
import TodoTemplate from './components/TodoTemplate';
import TodoList from './components/TodoList';
import TodoInsert from './components/TodoInsert';


function App() {

  // 성능 개선 hook 사용 테스트
  // function createTestTodos() {
  //   const todos = [];
  //   for (var i = 1; i <= 3000; i++) {
  //     todos.push({ id: i, title: `할 일 ${i}`, checked: false });
  //   }
  //   return todos;
  // }
  // const [ todos, setTodos ] = useState(createTestTodos);
  // const [ nextId, setNextId ] = useState(3001);
  // end of 성능 개성 hook 사용 테스트 

  // const [ todos, setTodos ] = useState([
  //   { id: 1, title: '스프링 웹 프로젝트', checked: true },
  //   { id: 2, title: '프론트엔드 웹 프로젝트', checked: false },
  //   { id: 3, title: '파이썬 프로젝트', checked: true },
  // ]);  
  
  const [ todos, setTodos ] = useState([]);
  const [ nextId, setNextId ] = useState(1);

  const insertTodo = (title) => {
    const todo = { id: nextId, title: title, checked: false }
    const newTodos = todos.concat(todo);
    setTodos(newTodos);
    setNextId(nextId  + 1); 

    // 서버에 데이터 전송 
    axios.post("http://127.0.0.1:8080/react-web/demo/add-todo", todo)
         .then( response => {

         })
         .catch( e => {

         });

  };

  const toggleTodoChecked = useCallback( (id) => {

    setTodos(
      // todos.map( (todo) => todo.id === id ? { id: todo.id, title: todo.title, checked: !todo.checked } : todo );
      (todos) => todos.map( (todo) => todo.id === id ? { ...todo, checked: !todo.checked } : todo )
    );

  }, []);

  const deleteTodo = useCallback( (id) => {
    setTodos(
      (todos) => todos.filter( (todo) => todo.id !== id )
    );
  }, []);

  return (
    <div>
      <TodoTemplate>
        <TodoInsert onInsert={ insertTodo } />
        <TodoList todos={ todos }
                  onToggle={ toggleTodoChecked }
                  onDelete={ deleteTodo }
        ></TodoList>
      </TodoTemplate>
    </div>
  );
}

export default App;
