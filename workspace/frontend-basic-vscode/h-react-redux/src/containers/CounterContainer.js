import Counter from "../components/Counter"

import { connect } from 'react-redux';
import { increase, decrease } from "../modules/CounterReducer";

const CounterContainer = ({ number, increase, decrease }) => {

    return (
        <Counter number={ number } 
                 onIncrease={ increase } 
                 onDecrease={ decrease } />
    );

};

const mapState = (state) => ({
    number: state.number
});
const mapDispatch = (dispatch) => ({
    increase: () => {
        dispatch(increase());
    },
    decrease: () => {
        dispatch(decrease());
    }
});

// export default CounterContainer;
export default connect({
    mapState, // 상태 정보
    mapDispatch // 상태 변경 액션
})(CounterContainer)