'use strict';

const e = React.createElement;

class ReactGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = { liked: false };
    }

    render() {
        if (this.state.liked) {
            return 'You liked this.';
        }

        return (
            <button onClick={() => this.setState({ liked: true })}>
                Like
            </button>
        );
    }
}

const domContainer = document.querySelector('#react_game_form');
ReactDOM.render(e(ReactGameForm), domContainer);