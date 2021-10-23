'use strict';

const e = React.createElement;

class ReactGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            gameName: "",
            package : "",
            selectedLibraries: []
        };
        this.handleSetGameName = this.handleSetGameName.bind(this);
        this.handleSetPackage = this.handleSetPackage.bind(this);
    }

    handleSetGameName(event){
        this.setState({gameName: event.target.value});
    }

    handleSetPackage(event){
        this.setState({package: event.target.value});
    }

    handleSubmit(event) {
        log(this.state);
    }

    render() {
        return <form onSubmit={this.handleSubmit}>
            <div className="form-group">
                <label htmlFor="gameName">Game Name</label>
                <input className="form-control" value={this.state.gameName} onChange={this.handleSetGameName} id="gameName" aria-describedby="gameNameHelp" placeholder="e.g. Asteroids"/>
                <small id="gameNameHelp" className="form-text text-muted">This will be the name of your project. Try to keep to english letters as it will also be used in your java source</small>
            </div>
            <div className="form-group">
                <label htmlFor="gamePackage">Package Name (Optional)</label>
                <input className="form-control" value={this.state.package} onChange={this.handleSetPackage} id="gamePackage" aria-describedby="gamePackageHelp" placeholder="e.g. com.mycompany"/>
                <small id="gameNameHelp" className="form-text text-muted">A package name keeps your classes unique. If you have a website it's traditionally the website backwards (all lower case). So myamazinggame.co.uk would become uk.co.myamazinggame. If you don't have a website choose something like that, or just leave it blank</small>
            </div>
            <button type="submit" className="btn btn-primary">Download a starter project</button>
        </form>
    }
}

const domContainer = document.querySelector('#react_game_form');
ReactDOM.render(e(ReactGameForm), domContainer);