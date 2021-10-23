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
            //these are groups which are determined by the server (e.g. networking)
            groupSelectedLibraries: {},
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

    handleSetLibrarySelectedInGroup =  (group, library)=>{
        let newSelectedLibraries = Object.assign({}, this.state.groupSelectedLibraries);
        newSelectedLibraries[group] = library;
        this.setState({groupSelectedLibraries:newSelectedLibraries});
    }

    isLibrarySelectedInGroup = (group, library) => {
        if ( group in this.state.groupSelectedLibraries ){
            return this.state.groupSelectedLibraries[group] === library;
        }else{
            return false;
        }
    }

    hasNoLibraryForGroup = (group) => {
        return !( group in this.state.groupSelectedLibraries ) || this.state.groupSelectedLibraries[group] == null;
    }

    renderPlatformRadios(){
        if (this.state.availableLibraryData === null){
            return <div/>
        }else{
            const platformRadios = [];

            this.state.availableLibraryData.jmePlatforms.forEach(platform => {
                platformRadios.push(<div className="form-check" key = {"platformRadioDiv" + platform.key}>
                    <input className="form-check-input" type="radio" name="platformRadios" id={"platformRadio" + platform.key} value={platform.key} checked = {this.state.platformLibrary === platform.key} onChange={this.handleSetPlatform} />
                    <label className="form-check-label" htmlFor={"platformRadio" + platform.key}>
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
            return <div/>
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

    renderExclusiveGroups(){

        if (this.state.availableLibraryData === null){
            return <div/>
        }else{
            const groups = [];
            this.state.availableLibraryData.specialCategories.forEach(specialCategory => {

                const thisGroupRadios = [];

                specialCategory.libraries.forEach(library => {
                    thisGroupRadios.push(<div className="form-check" key = {"platformRadioDiv" + library.key}>
                        <input className="form-check-input" type="radio" name={specialCategory.category.key+"Radios"} id={specialCategory.category.key+"Radio" + library.key} value={library.key} checked = {this.isLibrarySelectedInGroup(specialCategory.category.key, library.key)} onChange={event => this.handleSetLibrarySelectedInGroup(specialCategory.category.key, library.key)} />
                        <label className="form-check-label" htmlFor={specialCategory.category.key+"Radio" + library.key}>
                            <b>{library.libraryName}</b>
                            <p>{library.libraryDescription}</p>
                        </label>
                    </div>)
                })

                thisGroupRadios.push(<div className="form-check" key = {"platformRadioDivNone"}>
                    <input className="form-check-input" type="radio" name={specialCategory.category.key+"Radios"} id={specialCategory.category.key+"RadioNone"} value="" checked = {this.hasNoLibraryForGroup(specialCategory.category.key)} onChange={event => this.handleSetLibrarySelectedInGroup(specialCategory.category.key, null)} />
                    <label className="form-check-label" htmlFor={specialCategory.category.key+"RadioNone"}>
                        <b>None</b>
                        <p>Do not include a library for this category</p>
                    </label>
                </div>)

                groups.push(<div key = {specialCategory.category.key}>
                    <h2>{specialCategory.category.categoryDisplayName}</h2>
                    <p>{specialCategory.category.categoryDescription}</p>
                    {thisGroupRadios}
                </div>)
            })
            return groups;
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

            {this.renderExclusiveGroups()}

            <br/>
            <button type="submit" className="btn btn-primary">Download a starter project</button>
        </form>
    }
}

const domContainer = document.querySelector('#react_game_form');
ReactDOM.render(e(ReactGameForm), domContainer);