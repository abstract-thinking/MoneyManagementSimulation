import React from "react";
import ReactDOM from "react-dom";
import {Router} from "@reach/router";
import {Nav, Navbar} from "react-bootstrap";
import RiskManagement from "./RiskManagement";
import SaleRecommendations from "./SalesRecommendations";
import SaleRecommendation from "./SaleRecommendation";
import PurchaseRecommendations from "./PurchaseRecommendations";

const App = () => {
    return (
        <>
            <Navbar bg="light" expand="lg">
                <Navbar.Toggle aria-controls="basic-navbar-nav"/>
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="mr-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href={`/riskManagements/1/sell-recommendations`}>Sell</Nav.Link>
                        <Nav.Link href={`/riskManagements/1/purchase-recommendations`}>Purchase</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
            <Router>
                <RiskManagement path="/" riskManagementId="1"/>
                <SaleRecommendations path="riskManagements/:riskId/sell-recommendations"/>
                <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId"/>
                <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations"/>
            </Router>
        </>
    );
};

ReactDOM.render(<App/>, document.getElementById("root"));

export default App;
