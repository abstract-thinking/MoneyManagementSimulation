import React from "react";
import ReactDOM from "react-dom";
import { Router } from "@reach/router";
import RiskManagement from "./RiskManagement";
import SaleRecommendations from "./SalesRecommendations";
import SaleRecommendation from "./SaleRecommendation";
import PurchaseRecommendations from "./PurchaseRecommendations";
import {Nav, Navbar} from "react-bootstrap";

const App = () => {

  const riskManagementId = "1";

  return (
    <div>
        <header>
            <Navbar bg="light" expand="lg">
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="mr-auto">
                        <Nav.Link href="/">Home</Nav.Link>
                        <Nav.Link href={`/riskManagements/${riskManagementId}/sell-recommendations`}>Sell</Nav.Link>
                        <Nav.Link href={`/riskManagements/${riskManagementId}/purchase-recommendations`}>Purchase</Nav.Link>
                    </Nav>
                </Navbar.Collapse>
            </Navbar>
        </header>
      <Router>
        <RiskManagement path="/" riskManagementId={riskManagementId} />
        <SaleRecommendations path="riskManagements/:riskId/sell-recommendations" />
        <SaleRecommendation path="riskManagements/:riskId/sell-recommendations/:investmentId" />
        <PurchaseRecommendations path="riskManagements/:riskId/purchase-recommendations" />
      </Router>
    </div>
  );
};

ReactDOM.render(<App />, document.getElementById("react"));

export default App;
