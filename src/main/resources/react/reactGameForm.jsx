'use strict';

const e = React.createElement;

class ReactGameForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            gameName: "",
            package : "",
            //all the libaries that aren't radios are in here.Its just a big list of selected library keys
            freeSelectLibraries: [],
            //these are groups which are determined by the server (e.g. networking). This is a map of group key -> selected library
            groupSelectedLibraries: {},
            //library key of the libary that supports desktop, VR etc
            platformLibraries: [],
            //this is the big bundle of data that comes down from the server to say what libraries are available, what the defaults are etc
            availableLibraryData : null,
            //if the user has clicked download (but not updated the data) a message is displayed. This controls that
            hasDownloaded: false,
            gradlePreview: null,
            validationMessage: null
        };
    }

    componentDidMount() {
        fetch('/jme-initializer/libraries')
            .then(res => res.json())
            .then((data) => {
                let stateUpdate = {
                    availableLibraryData: data,
                    freeSelectLibraries:data.defaultSelectedFreeChoiceLibraries,
                    platformLibraries:[data.defaultPlatform]
                };
                //add defaults (if available) for the groupSelectedLibraries
                let groupSelectedLibraries = {};
                data.specialCategories.forEach( specialCategoryData => {
                    groupSelectedLibraries[specialCategoryData.category.key] = specialCategoryData.defaultLibrary;
                })
                stateUpdate.groupSelectedLibraries = groupSelectedLibraries;

                this.setState(stateUpdate)
            })
            .catch(console.log)
    }

    handleSetGameName = (event) => {
        this.setState({gameName: event.target.value, hasDownloaded:false, validationMessage:null});
    }

    handleSetPackage = (event) => {
        this.setState({package: event.target.value, hasDownloaded:false, validationMessage:null});
    }

    handleTogglePlatformLibrary = (libraryKey) => {
        let currentlySelected = this.state.platformLibraries.includes(libraryKey)
        if (currentlySelected){
            let newFreeSelectLibraries = this.state.platformLibraries.filter( v => v !== libraryKey )
            this.setState({platformLibraries: newFreeSelectLibraries, hasDownloaded:false, validationMessage:null });
        }else{
            this.setState({platformLibraries: [...this.state.platformLibraries, libraryKey], hasDownloaded:false, validationMessage:null});
        }
    }

    handleToggleFreeFormLibrary = (libraryKey) => {
        let currentlySelected = this.state.freeSelectLibraries.includes(libraryKey)
        if (currentlySelected){
            let newFreeSelectLibraries = this.state.freeSelectLibraries.filter( v => v !== libraryKey )
            this.setState({freeSelectLibraries: newFreeSelectLibraries, hasDownloaded:false, validationMessage:null });
        }else{
            this.setState({freeSelectLibraries: [...this.state.freeSelectLibraries, libraryKey], hasDownloaded:false, validationMessage:null});
        }
    }

    handleSubmit = (event) =>  {
        event.preventDefault(); //don't refresh the page
        if (this.validate()) {
            this.setState({hasDownloaded: true});
            //doesn't "actually" change the page location because its a download link
            location.href = "/jme-initializer/zip?" + this.formOptionsQueryString();
        }
    }

    formOptionsQueryString = () => {
        return "gameName=" + encodeURIComponent(this.state.gameName) + "&packageName=" +encodeURIComponent(this.state.package)+"&libraryList=" + encodeURIComponent(this.getRequiredLibrariesAsCommaSeperatedList())
    }

    handleSetLibrarySelectedInGroup =  (group, library)=>{
        let newSelectedLibraries = Object.assign({}, this.state.groupSelectedLibraries);
        newSelectedLibraries[group] = library;
        this.setState({groupSelectedLibraries:newSelectedLibraries});
    }

    getRequiredLibrariesAsCommaSeperatedList = () => {
        let fullRequiredLibrarys = []
        fullRequiredLibrarys = fullRequiredLibrarys.concat(this.state.platformLibraries);
        fullRequiredLibrarys = fullRequiredLibrarys.concat(this.state.freeSelectLibraries);
        for(const categoryKey in this.state.groupSelectedLibraries){
            const library = this.state.groupSelectedLibraries[categoryKey];
            if (library != null){
                fullRequiredLibrarys.push(library);
            }
        }

        return fullRequiredLibrarys.join(",");
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

    fetchGradlePreview = (event) => {

        if (this.validate()) {
            event.preventDefault(); //don't submit the form

            fetch('/jme-initializer/gradle-preview?' + this.formOptionsQueryString())
                .then(response => response.json())
                .then((data) => {
                    console.log(data);
                    this.setState({gradlePreview: data});
                })
                .catch(console.log)
        }
    }

    /**
     * Validates the form, returns if its valid (and also updates the state to show a validation message if its invalid)
     */
    validate = () => {
        if (this.state.platformLibraries.length === 0){
            this.setState({ validationMessage:"You must select at least 1 platform" });
            return false
        }

        if (this.state.gameName === ""){
            this.setState({ validationMessage:"The game must have a name" });
            return false
        }

        return true;
    }

    /**
     * Given a list of required platform keys (empty means no requirements) returns if at least one of those is
     * selected
     * @param requiredPlatformList
     */
    platformAvailable = (requiredPlatformList) => {
        if (requiredPlatformList.length === 0){
            return true;
        }

        const availableRequiredPlatforms = requiredPlatformList.filter(value => this.state.platformLibraries.includes(value));

        return availableRequiredPlatforms.length > 0;
    }

    renderRequiredPlatformStatement(requiredPlatformList){

        if (requiredPlatformList.length === 0){
            return;
        }

        const platformStrings = [];
        this.state.availableLibraryData.jmePlatforms.forEach(platform => {
            if (requiredPlatformList.includes(platform.key)){
                platformStrings.push(platform.libraryName);
            }
        });

        return <p><small>{"This library is only applicable for platform(s): " + platformStrings.join(", ") }</small></p>
    }

    renderPlatformCheckboxes(){
        if (this.state.availableLibraryData === null){
            return <div/>
        }else{
            const platformCheckboxes = [];

            this.state.availableLibraryData.jmePlatforms.forEach(platform => {
                platformCheckboxes.push(<div className="form-check" key = {"platformRadioDiv" + platform.key}>
                    <input className="form-check-input" type="checkbox" name="platformRadios" id={"platformRadio" + platform.key} value={platform.key} checked = {this.state.platformLibraries.includes(platform.key)} onChange={event => this.handleTogglePlatformLibrary(platform.key)} />
                    <label className="form-check-label" htmlFor={"platformRadio" + platform.key}>
                        <b>{platform.libraryName}</b>
                        <p>{platform.libraryDescription}</p>
                    </label>
                </div>);

            });
            return <div onChange={this.handleSetPlatform}>
                {platformCheckboxes}
                <small>Select at least 1 platform</small>
            </div>;
        }
    }

    renderFreeFormJmeLibraries(){
        if (this.state.availableLibraryData === null){
            return <div/>
        }else{
            const jmeLibraryChecks = [];

            this.state.availableLibraryData.jmeGeneralLibraries.forEach(library => {
                jmeLibraryChecks.push(<div className="form-check" key = {"libraryCheckDiv" + library.key}>
                    <input disabled = {!this.platformAvailable(library.requiredPlatforms)} className="form-check-input" type="checkbox" value={library.key} id={"platformCheck" + library.key} checked = {this.platformAvailable(library.requiredPlatforms) && this.state.freeSelectLibraries.includes(library.key)} onChange = {event => this.handleToggleFreeFormLibrary(library.key)} />
                        <label className="form-check-label" htmlFor={"platformCheck" + library.key}>
                            <b>{library.libraryName}</b>
                            <p>{library.libraryDescription}</p>
                        </label>
                </div>);
            });
            return jmeLibraryChecks;
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
                        <input disabled = {!this.platformAvailable(library.requiredPlatforms)} className="form-check-input" type="radio" name={specialCategory.category.key+"Radios"} id={specialCategory.category.key+"Radio" + library.key} value={library.key} checked = { this.platformAvailable(library.requiredPlatforms) && this.isLibrarySelectedInGroup(specialCategory.category.key, library.key)} onChange={event => this.handleSetLibrarySelectedInGroup(specialCategory.category.key, library.key)} />
                        <label className="form-check-label" htmlFor={specialCategory.category.key+"Radio" + library.key}>
                            <b>{library.libraryName}</b>
                            <p>{library.libraryDescription}</p>
                        </label>
                        {this.renderRequiredPlatformStatement(library.requiredPlatforms)}
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

    renderOtherLibraries(){
        if (this.state.availableLibraryData === null){
            return <div/>
        }else{
            const libraryChecks = [];

            this.state.availableLibraryData.generalLibraries.forEach(library => {
                libraryChecks.push(<div className="form-check" key = {"libraryDiv" + library.key}>
                    <input disabled = {!this.platformAvailable(library.requiredPlatforms)} className="form-check-input" type="checkbox" value={library.key} id={"libraryCheck" + library.key} checked = {this.platformAvailable(library.requiredPlatforms) && this.state.freeSelectLibraries.includes(library.key)} onChange = {event => this.handleToggleFreeFormLibrary(library.key)} />
                    <label className="form-check-label" htmlFor={"libraryCheck" + library.key}>
                        <b>{library.libraryName}</b>
                        <p>{library.libraryDescription}</p>
                        {this.renderRequiredPlatformStatement(library.requiredPlatforms)}
                    </label>

                </div>);
            });
            return libraryChecks;
        }
    }

    renderGradlePreview(){
        let numberOfFiles = Object.keys(this.state.gradlePreview).length;

        var introductoryText = "";
        if (numberOfFiles === 1){
            introductoryText = "This is the build file for your requested libraries. The full download will contain this and other important files";
        }else{
            introductoryText = "This is the build files for your requested libraries (As a multiplatform project there are multiple build.gradle files). The full download will contain this and other important files";
        }

        const gradleFiles = [];
        for( var path in this.state.gradlePreview){

            if (this.state.gradlePreview.hasOwnProperty(path)) {
                var content = this.state.gradlePreview[path];

                gradleFiles.push(<div key = {path}>
                    {numberOfFiles >1 && <h3>{path}</h3>}
                    <div style={{backgroundColor: "black"}}>
                        <pre className="pre-scrollable" style={{marginLeft: "2px"}}>
                            <code style={{color: "white"}}>{content}</code>
                        </pre>
                    </div>
                </div>)
            }
        }


        return <div>
            <h2>build.gradle preview</h2>
            <p>{introductoryText}</p>
            {gradleFiles}
        </div>

    }

    render() {
        return <form onSubmit={this.handleSubmit}>
            <div className="form-group">
                <label htmlFor="gameName"><b>Application Name</b></label>
                <input className="form-control" value={this.state.gameName} onChange={this.handleSetGameName} id="gameName" aria-describedby="gameNameHelp" placeholder="e.g. Asteroids" required/>
                <small id="gameNameHelp" className="form-text">This will be the name of your project. Try to keep to english letters as it will also be used in your java source</small>
            </div>
            <div className="form-group">
                <label htmlFor="gamePackage"><b>Package Name (Optional)</b></label>
                <input className="form-control" value={this.state.package} onChange={this.handleSetPackage} id="gamePackage" aria-describedby="gamePackageHelp" placeholder="e.g. com.mycompany"/>
                <small id="gameNameHelp" className="form-text">A package name keeps your classes unique. If you have a website it's traditionally the website backwards (all lower case). So myamazinggame.co.uk would become uk.co.myamazinggame. If you don't have a website choose something like that, or just leave it blank</small>
            </div>

            <h2>
                Platform
            </h2>
            <p>JMonkeyEngine can target many platforms, select the platform(s) your application will target</p>
            {this.renderPlatformCheckboxes()}

            <br/> <br/>
            <div className="alert alert-secondary" role="alert">
                Don't worry if you're not sure about libraries, you can always add more later
            </div>
            <h2>
                Additional JME libraries
            </h2>
            <p>Essential JME libraries are included by default but select any more that may be useful here</p>
            {this.renderFreeFormJmeLibraries()}

            {this.renderExclusiveGroups()}

            <h2>Other</h2>
            <p>Libraries often found to be useful in JME applications</p>
            {this.renderOtherLibraries()}

            <br/>

            { this.state.gradlePreview !== null && this.renderGradlePreview() }

            {this.state.hasDownloaded && <div className="alert alert-success" role="alert">
                <p>A zip will now download. Unzip it and use it as a starter project in the IDE of your choice.</p>
                <p>IntelliJ, Android Studio and Eclipse will support this project by default, Netbeans will support it with the Gradle plugin installed</p>
            </div>}
            {this.state.validationMessage && <div className="alert alert-danger" role="alert">
                <p>{this.state.validationMessage}</p>
            </div> }
            <div className="btn-group" role="group" aria-label="Basic example">
                <button type="submit" className="btn btn-primary mr-2">Download a starter project</button>
                <button className="btn btn-secondary mr-2" onClick={this.fetchGradlePreview}>Preview build.gradle file</button>
            </div>
            <br/>

        </form>
    }
}

$(document).ready(function () {
    const domContainer = document.querySelector('#react_game_form');
    ReactDOM.render(e(ReactGameForm), domContainer);
})
