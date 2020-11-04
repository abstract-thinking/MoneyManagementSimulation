const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {investments: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/investments'}).done(
            response => {
                this.setState({investments: response.entity._embedded.investments});
        });
    }

    render() {
        return (
            <InvestmentList investments={this.state.investments}/>
        )
    }
}

class MoneyManagement extends React.Component {



    render() {
        const investments = this.props.investments.map(
            investment => <Investment key={investment._links.self.href} investment={investment}/>
        );
        return (
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
                    </tr>
                    {investments}
                </tbody>
            </table>
        )
    }
}

class InvestmentList extends React.Component {
    render() {
        const investments = this.props.investments.map(
            investment => <Investment key={investment._links.self.href} investment={investment}/>
        );
        return (
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
                    </tr>
                    {investments}
                </tbody>
            </table>
        )
    }
}

class Investment extends React.Component{
    render() {
        return (
            <tr>
                <td>{this.props.investment.name}</td>
                <td>{this.props.investment.quantity}</td>
                <td>{this.props.investment.purchasePrice}</td>
                <td>{this.props.investment.notionalSalesPrice}</td>
                <td>{this.props.investment.purchaseCost}</td>
                <td>{this.props.investment.notionalRevenue}</td>
                <td>{this.props.investment.sum}</td>
                <td>{this.props.investment.profitOrLoss}</td>
            </tr>
        )
    }
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
)