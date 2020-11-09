const React = require('react');
const ReactDOM = require('react-dom');
const when = require('when');
const client = require('./client');

const follow = require('./follow'); // function to hop multiple links by "rel"

const root = '/api';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {moneyManagements: [], investments: [], attributes: [], links: {}};

        this.onCreate = this.onCreate.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
    }

    loadFromServer() {
        follow(client, root, [ // <1>
            {rel: 'moneyManagements'}]
        ).then(moneyManagementCollection => { // <2>
            return client({
                              method: 'GET',
                              path: moneyManagementCollection.entity._links.profile.href,
                              headers: {'Accept': 'application/schema+json'}
                          }).then(schema => {
                this.schema = schema.entity;
                this.store = moneyManagementCollection.entity._embedded.moneyManagements
                console.log('MM 1: ' + this.store)
                return moneyManagementCollection;
            });
        }).then(moneyManagementCollection => {
            return client({
                              method: 'GET',
                              path: moneyManagementCollection.entity._embedded.moneyManagements[0]._links.investments.href,
                              headers: {'Accept': 'application/json'}
                          }).then(investmentCollection => {
                // this.schema = schema.entity;
                this.store = moneyManagementCollection.entity._embedded.moneyManagements
                console.log('MM 2: ' + this.store)

                this.links = investmentCollection.entity._links;
                return investmentCollection;
            });

        }).then(investmentCollection => { // <2>
            return client({
                              method: 'GET',
                              path: 'http://localhost:8080/api/profile/investments',
                              headers: {'Accept': 'application/schema+json'}
                          }).then(schema => {
                this.schema = schema.entity;
                this.schema.properties = Object.fromEntries(
                    Object.entries(schema.entity.properties).filter(([k, v]) => v.readOnly === false));
                delete this.schema.properties['moneyManagement']
                return investmentCollection;
            });
        }).then(investmentCollection => { // <3>
            return investmentCollection.entity._embedded.investments.map(investment =>
                                                                             client({
                                                                                        method: 'GET',
                                                                                        path: investment._links.self.href
                                                                                    })
            );
        }).then(investmentPromises => { // <4>
            return when.all(investmentPromises);
        }).done(investments => { // <5>
            this.setState({
                              investments: investments,
                              moneyManagements: this.store,
                              attributes: Object.keys(this.schema.properties),
                              links: this.links
                          });
        });
    }

    onCreate(newInvestment) {
        const self = this;
        follow(client, root, ['investments']).then(response => {
            return client({
                              method: 'POST',
                              path: response.entity._links.self.href,
                              entity: newInvestment,
                              headers: {'Content-Type': 'application/json'}
                          })
        }).then(response => {
            return follow(client, root, [{rel: 'investments'}]);
        }).done(response => {

        });
    }

    onUpdate(investment, updatedInvestment) {
        client({
                   method: 'PUT',
                   path: investment.entity._links.self.href,
                   entity: updatedInvestment,
                   headers: {
                       'Content-Type': 'application/json',
                       'If-Match': investment.headers.Etag
                   }
               }).done(response => {
            this.loadFromServer();
        }, response => {
            if (response.status.code === 412) {
                alert('DENIED: Unable to update ' +
                      investment.entity._links.self.href + '. Your copy is stale.');
            }
        });
    }

    onDelete(investment) {
        client({
                   method: 'DELETE',
                   path: investment.entity._links.self.href
               }).done(response => {
            this.loadFromServer();
        });
    }

    componentDidMount() {
        this.loadFromServer();
    }

    render() {
        return (
            <div>
                <MoneyManagements moneyManagements={this.state.moneyManagements}/>
                <InvestmentList investments={this.state.investments}
                                links={this.state.links}
                                attributes={this.state.attributes}
                                onUpdate={this.onUpdate}
                                onDelete={this.onDelete}/>
            </div>

        )
    }
}

class MoneyManagements extends React.Component {

    render() {
        console.log('In MoneyManagements ' + this.props.moneyManagements)

        const moneyManagements = this.props.moneyManagements.map(
            moneyManagement => <MoneyManagement moneyManagement={moneyManagement}/>);

        return <div>
            {moneyManagements}
        </div>
    }
}

class MoneyManagement extends React.Component {
    render() {
        return (
            <div>
                <h1>{this.props.moneyManagement.totalCapital}</h1>
                <h1>{this.props.moneyManagement.individualPositionRiskInPercent}</h1>
                <h1>{this.props.moneyManagement.individualPositionRisk}</h1>
                <h1>{this.props.moneyManagement.portfolioRiskInPercent}</h1>
                <h1>{this.props.moneyManagement.totalRiskInPercent}</h1>
                <h1>{this.props.moneyManagement.totalSum}</h1>
                <h1>{this.props.moneyManagement.totalRevenue}</h1>
                <h1>{this.props.moneyManagement.totalLossAbs}</h1>
            </div>
        )
    }
}

class CreateDialog extends React.Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        const newInvestment = {};
        this.props.attributes.forEach(attribute => {
            newInvestment[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
        });
        this.props.onCreate(newInvestment);
        this.props.attributes.forEach(attribute => {
            ReactDOM.findDOMNode(this.refs[attribute]).value = ''; // clear out the dialog's inputs
        });
        window.location = "#";
    }

    render() {
        const inputs = this.props.attributes.map(attribute =>
                                                     <p key={attribute}>
                                                         <input type="text" placeholder={attribute} ref={attribute} className="field"/>
                                                     </p>
        );
        return (
            <div>
                <a href="#createInvestment">Create</a>

                <div id="createInvestment" className="modalDialog">
                    <div>
                        <a href="#" title="Close" className="close">X</a>
                        <h2>Create new investment</h2>
                        <form>
                            {inputs}
                            <button onClick={this.handleSubmit}>Create</button>
                        </form>
                    </div>
                </div>
            </div>
        )
    }
}

class UpdateDialog extends React.Component {

    constructor(props) {
        super(props);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleSubmit(e) {
        e.preventDefault();
        const updatedInvestment = {};
        this.props.attributes.forEach(attribute => {
            updatedInvestment[attribute] = ReactDOM.findDOMNode(this.refs[attribute]).value.trim();
        });
        this.props.onUpdate(this.props.investment, updatedInvestment);
        window.location = "#";
    }

    render() {
        const inputs = this.props.attributes.map(attribute =>
                                                     <p key={this.props.investment.entity[attribute]}>
                                                         <input type="text"
                                                                placeholder={attribute}
                                                                defaultValue={this.props.investment.entity[attribute]}
                                                                ref={attribute}
                                                                className="field"/>
                                                     </p>
        );

        const dialogId = "updateInvestment-" + this.props.investment.entity._links.self.href;

        return (
            <div key={this.props.investment.entity._links.self.href}>
                <a href={"#" + dialogId}>Update</a>
                <div id={dialogId} className="modalDialog">
                    <div>
                        <a href="#" title="Close" className="close">X</a>

                        <h2>Update an investment</h2>

                        <form>
                            {inputs}
                            <button onClick={this.handleSubmit}>Update</button>
                        </form>
                    </div>
                </div>
            </div>
        )
    }
}

class InvestmentList extends React.Component {

    render() {
        const investments = this.props.investments.map(investment =>
                                                           <Investment key={investment.entity._links.self.href}
                                                                       investment={investment}
                                                                       attributes={this.props.attributes}
                                                                       onUpdate={this.props.onUpdate}
                                                                       onDelete={this.props.onDelete}/>
        );

        return (
            <div>
                <table>
                    <tbody>
                    <tr>
                        <th>Name</th>
                        <th>Quantity</th>
                        <th>Purchase Price</th>
                        <th>Notional Sales Price</th>
                        <th>Purchase Cost</th>
                        <th>Notional Revenue</th>
                        <th>Sum</th>
                        <th>Profit or Loss</th>
                        <th></th>
                        <th></th>
                    </tr>
                    {investments}
                    </tbody>
                </table>
            </div>
        )
    }
}

class Investment extends React.Component {
    constructor(props) {
        super(props);
        this.handleDelete = this.handleDelete.bind(this);
    }

    handleDelete() {
        this.props.onDelete(this.props.investment);
    }

    render() {
        return (
            <tr>
                <td>{this.props.investment.entity.name}</td>
                <td>{this.props.investment.entity.quantity}</td>
                <td>{this.props.investment.entity.purchasePrice}</td>
                <td>{this.props.investment.entity.notionalSalesPrice}</td>
                <td>{this.props.investment.entity.purchaseCost}</td>
                <td>{this.props.investment.entity.notionalRevenue}</td>
                <td>{this.props.investment.entity.sum}</td>
                <td>{this.props.investment.entity.profitOrLoss}</td>
                <td>
                    <UpdateDialog investment={this.props.investment}
                                  attributes={this.props.attributes}
                                  onUpdate={this.props.onUpdate}/>
                </td>
                <td>
                    <button onClick={this.handleDelete}>Delete</button>
                </td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App/>,
    document.getElementById('react')
)