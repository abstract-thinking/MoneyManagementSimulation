import React, { useState, useEffect } from "react";
import { Container, Spinner, Table } from "react-bootstrap";
import axios from "axios";
import Investment from "./Investment";
import NavigationBar from "./NavigationBar";

const RiskManagement = ({ riskManagementId }) => {
  const [riskManagement, setRiskManagement] = useState("");
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    setLoading(true);
    setError("");

    axios
      .get("http://localhost:8080/api/risks/" + riskManagementId)
      .then(response => {
        setRiskManagement(response.data);
        console.log("Received data: ", response.data);
        setLoading(false);
      })
      .catch(error => {
        console.log("Error: ", error);

        setLoading(false);
        setError("Error: " + error);
      });
  }, []);

  if (isLoading) {
    return (
      <Container
        className="d-flex align-items-center justify-content-center"
        style={{ minHeight: "100vh" }}
      >
        <Spinner animation="grow" role="status">
          <span className="sr-only">Loading...</span>
        </Spinner>
      </Container>
    );
  }

  if (error !== "") {
    return <p>ERROR: {error}</p>;
  }

  if (riskManagement === "") {
    return <p>loading starts now</p>;
  }

  return (
    <div className="container">
      <NavigationBar riskManagementId={riskManagementId} />
      <Table striped bordered>
        <tbody>
          <tr>
            <td className="header">Gesamtkapital</td>
            <td>{riskManagement.totalCapital.toFixed(2)}</td>
          </tr>
          <tr>
            <td className="header">Einzelpositionrisiko</td>
            <td>{riskManagement.individualPositionRiskInPercent.toFixed(2)}</td>
          </tr>
          <tr>
            <td />
            <td>{riskManagement.individualPositionRisk.toFixed(2)}</td>
          </tr>
          <tr>
            <td className="header">WKN</td>
            <td className="header">Name</td>
            <td className="header">Stück</td>
            <td className="header">Kaufkurs</td>
            <td className="header">Einstiegssumme</td>
            <td className="header">Fiktiver Verkaufspreis</td>
            <td className="header">Kosten</td>
            <td className="header">Fiktiver Verkaufserlös</td>
            <td className="header">Risiko</td>
          </tr>
          {riskManagement.investments &&
            riskManagement.investments.map(investment => (
              <Investment
                key={investment.name}
                riskManagementId={riskManagementId}
                investment={investment}
              />
            ))}
          <tr>
            <td className="header">Depotwert</td>
            <td colSpan={3} />
            <td className="number-content">
              {riskManagement.totalInvestment.toFixed(2)}
            </td>
            <td colSpan={2} />
            <td className="number-content">
              {riskManagement.totalRevenue.toFixed(2)}
            </td>
            <td className="number-content">
              {riskManagement.depotRisk.toFixed(2)}
            </td>
          </tr>
          <tr>
            <td className="header">Depotrisiko</td>
            <td colSpan={7} />
            <td className="number-content">
              {riskManagement.depotRiskInPercent.toFixed(2)}
            </td>
          </tr>
          <tr>
            <td className="header">Gesamtrisiko</td>
            <td className="number-content">
              {riskManagement.totalRiskInPercent.toFixed(2)}
            </td>
          </tr>
        </tbody>
      </Table>
    </div>
  );
};

export default RiskManagement;
