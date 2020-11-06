const React = require('react');
const ReactDOM = require('react-dom');
const when = require('when');
const client = require('./client');

const follow = require('./follow'); // function to hop multiple links by "rel"

const root = '/api';

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {investments: [], attributes: [], pageSize: 2, links: {}};

        this.updatePageSize = this.updatePageSize.bind(this);
        this.onCreate = this.onCreate.bind(this);
        this.onUpdate = this.onUpdate.bind(this);
        this.onDelete = this.onDelete.bind(this);
        this.onNavigate = this.onNavigate.bind(this);
    }

    loadFromServer(pageSize) {
        follow(client, root, [ // <1>
            {rel: 'investments', params: {size: pageSize}}]
        ).then(investmentCollection => { // <2>
            return client({
                              method: 'GET',
                              path: investmentCollection.entity._links.profile.href,
                              headers: {'Accept': 'application/schema+json'}
                          }).then(schema => {
                this.schema = schema.entity;
                this.links = investmentCollection.entity._links;
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
                              attributes: Object.keys(this.schema.properties),
                              pageSize: pageSize,
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
            return follow(client, root, [{rel: 'investments', params: {'size': self.state.pageSize}}]);
        }).done(response => {
            if (typeof response.entity._links.last !== "undefined") {
                this.onNavigate(response.entity._links.last.href);
            } else {
                this.onNavigate(response.entity._links.self.href);
            }
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
            this.loadFromServer(this.state.pageSize);
        }, response => {
            if (response.status.code === 412) {
                alert('DENIED: Unable to update ' +
                      investment.entity._links.self.href + '. Your copy is stale.');
            }
        });
    }

    onDelete(investment) {
        client({method: 'DELETE', path: investment.entity._links.self.href}).done(response => {
            this.loadFromServer(this.state.pageSize);
        });
    }

    onNavigate(navUri) {
        client({
                   method: 'GET',
                   path: navUri
               }).then(investmentCollection => {
            this.links = investmentCollection.entity._links;

            return investmentCollection.entity._embedded.investments.map(investment =>
                                                                             client({
                                                                                        method: 'GET',
                                                                                        path: investment._links.self.href
                                                                                    })
            );
        }).then(investmentPromises => {
            return when.all(investmentPromises);
        }).done(investments => {
            this.setState({
                              investments: investments,
                              attributes: Object.keys(this.schema.properties),
                              pageSize: this.state.pageSize,
                              links: this.links
                          });
        });
    }

    updatePageSize(pageSize) {
        if (pageSize !== this.state.pageSize) {
            this.loadFromServer(pageSize);
        }
    }

    componentDidMount() {
        console.log('I was triggered during componentDidMount')
        this.loadFromServer(this.state.pageSize);
    }

    render() {
        return (
            <div>
                <CreateDialog attributes={this.state.attributes} onCreate={this.onCreate}/>
                <InvestmentList investments={this.state.investments}
                                links={this.state.links}
                                pageSize={this.state.pageSize}
                                attributes={this.state.attributes}
                                onNavigate={this.onNavigate}
                                onUpdate={this.onUpdate}
                                onDelete={this.onDelete}
                                updatePageSize={this.updatePageSize}/>
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
                                                         <input type="text" placeholder={attribute}
                                                                defaultValue={this.props.investment.entity[attribute]}
                                                                ref={attribute} className="field"/>
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

    constructor(props) {
        super(props);
        this.handleNavFirst = this.handleNavFirst.bind(this);
        this.handleNavPrev = this.handleNavPrev.bind(this);
        this.handleNavNext = this.handleNavNext.bind(this);
        this.handleNavLast = this.handleNavLast.bind(this);
        this.handleInput = this.handleInput.bind(this);
    }

    handleInput(e) {
        e.preventDefault();
        const pageSize = ReactDOM.findDOMNode(this.refs.pageSize).value;
        if (/^[0-9]+$/.test(pageSize)) {
            this.props.updatePageSize(pageSize);
        } else {
            ReactDOM.findDOMNode(this.refs.pageSize).value = pageSize.substring(0, pageSize.length - 1);
        }
    }

    handleNavFirst(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.first.href);
    }

    handleNavPrev(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.prev.href);
    }

    handleNavNext(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.next.href);
    }

    handleNavLast(e) {
        e.preventDefault();
        this.props.onNavigate(this.props.links.last.href);
    }

    render() {
        const investments = this.props.investments.map(investment =>
                                                           <Investment key={investment.entity._links.self.href}
                                                                       investment={investment}
                                                                       attributes={this.props.attributes}
                                                                       onUpdate={this.props.onUpdate}
                                                                       onDelete={this.props.onDelete}/>
        );

        const navLinks = [];
        if ("first" in this.props.links) {
            navLinks.push(<button key="first" onClick={this.handleNavFirst}>&lt;&lt;</button>);
        }
        if ("prev" in this.props.links) {
            navLinks.push(<button key="prev" onClick={this.handleNavPrev}>&lt;</button>);
        }
        if ("next" in this.props.links) {
            navLinks.push(<button key="next" onClick={this.handleNavNext}>&gt;</button>);
        }
        if ("last" in this.props.links) {
            navLinks.push(<button key="last" onClick={this.handleNavLast}>&gt;&gt;</button>);
        }

        return (
            <div>
                <input ref="pageSize" defaultValue={this.props.pageSize} onInput={this.handleInput}/>
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
                <div>
                    {navLinks}
                </div>
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