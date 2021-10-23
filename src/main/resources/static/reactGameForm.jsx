'use strict';

const e = React.createElement;

class ReactGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            gameName: "",
            package : "",
            //all the libaries that aren't radios are in here
            freeSelectLibraries: [],
            platformLibrary: null,
            availableLibraryData : null
        };
    }

    componentDidMount() {
        fetch('/jme-initialiser/libraries')
            .then(res => res.json())
            .then((data) => {
                this.setState({ availableLibraryData: data })
            })
            .catch(console.log)
    }

    handleSetGameName = (event) => {
        this.setState({gameName: event.target.value});
    }

    handleSetPackage = (event) => {
        this.setState({package: event.target.value});
    }

    handleSetPlatform = (event) => {
        this.setState({platformLibrary: event.target.value});
    }

    handleToggleFreeFormLibrary = (libraryKey) => {
        let currentlySelected = this.state.freeSelectLibraries.includes(libraryKey)
        if (currentlySelected){
            let newFreeSelectLibraries = this.state.freeSelectLibraries.filter( v => v !== libraryKey )
            this.setState({freeSelectLibraries: newFreeSelectLibraries});
        }else{
            this.setState({freeSelectLibraries: [...this.state.freeSelectLibraries, libraryKey]});
        }
    }

    handleSubmit = (event) =>  {
        console.log(this.state);
    }

    renderPlatformRadios(){
        if (this.state.availableLibraryData === null){
            return <div></div>
        }else{
            const platformRadios = [];

            this.state.availableLibraryData.jmePlatforms.forEach(platform => {
                platformRadios.push(<div className="form-check" key = {"platformRadioDiv" + platform.key}>
                    <input className="form-check-input" type="radio" name="platformRadios" id={"platformRadio" + platform.key} value={platform.key} checked = {this.state.platformLibrary === platform.key} />
                    <label className="form-check-label ml-3 text-sm font-medium text-gray-700" htmlFor={"platformRadio" + platform.key}>
                        <b>{platform.libraryName}</b>
                        <p>{platform.libraryDescription}</p>
                    </label>
                </div>);
            });
            return <div onChange={this.handleSetPlatform}>{platformRadios}</div>;
        }
    }

    renderFreeFormJmeLibraries(){
        if (this.state.availableLibraryData === null){
            return <div></div>
        }else{
            const jmeLibraryRadios = [];

            this.state.availableLibraryData.jmeGeneralLibraries.forEach(platform => {
                jmeLibraryRadios.push(<div className="form-check" key = {"platformRadioDiv" + platform.key}>
                    <input className="form-check-input" type="checkbox" value={platform.key} id={"platformCheck" + platform.key} checked = {this.state.freeSelectLibraries.includes(platform.key)} onChange = {event => this.handleToggleFreeFormLibrary(platform.key)} />
                        <label className="form-check-label" htmlFor={"platformCheck" + platform.key}>
                            <b>{platform.libraryName}</b>
                            <p>{platform.libraryDescription}</p>
                        </label>
                </div>);
            });
            return jmeLibraryRadios;
        }
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

            <h2>
                Platform
            </h2>
            <p>JMonkeyEngine can target many platforms, select the platform your game will target</p>
            {this.renderPlatformRadios()}

            <h2>
                Additional JME libraries
            </h2>
            <p>Essential JME libraries are included by default but select any more that may be useful here</p>
            {this.renderFreeFormJmeLibraries()}

            <br/>
            <button type="submit" className="btn btn-primary">Download a starter project</button>
        </form>
    }
}

const domContainer = document.querySelector('#react_game_form');
ReactDOM.render(e(ReactGameForm), domContainer);