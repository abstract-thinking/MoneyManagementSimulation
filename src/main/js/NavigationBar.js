import React from "react";
import { Nav, Navbar } from "react-bootstrap";

const NavigationBar = ({ riskManagementId }) => {
  return (
    <Navbar bg="light" expand="lg">
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="mr-auto">
          <Nav.Link href={`/riskManagements/${riskManagementId}`}>
            Home
          </Nav.Link>
          <Nav.Link
            href={`/riskManagements/${riskManagementId}/sell-recommendations`}
          >
            Verkaufen
          </Nav.Link>
          <Nav.Link
            href={`/riskManagements/${riskManagementId}/purchase-recommendations`}
          >
            Kaufen
          </Nav.Link>
          <Nav.Link href={`/riskManagements/${riskManagementId}/modifier`}>
            Ändern
          </Nav.Link>
          <Nav.Link href={`/riskManagements/${riskManagementId}/calculation`}>
            Positionsgröße
          </Nav.Link>
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default NavigationBar;
