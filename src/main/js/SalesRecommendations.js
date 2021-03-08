import React, {useState, useEffect} from "react";
import axios from "axios";

import SaleRecommendation from "./SaleRecommendation";
import {Container, Spinner, Table} from "react-bootstrap";

const SalesRecommendations = ({riskId}) => {
    const [result, setResult] = useState(null);
    const [isLoading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const targetUrl = `http://localhost:8080/api/risks/${riskId}/recommendations/sales`;

    useEffect(() => {
        setLoading(true);
        setError("");

        axios
            .get(targetUrl)
            .then(response => {
                setResult(response.data);
                console.log("Received data: ", response.data);
                setLoading(false);
            })
            .catch(error => {
                console.log("Error: ", error);

                setLoading(false);
                setError("Error: " + error);
            });
    }, [targetUrl]);

    if (isLoading) {
        return (
            <Container
                className="d-flex align-items-center justify-content-center"
                style={{minHeight: "100vh"}}
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

    if (result === null) {
        return <p>loading starts now</p>;
    }

    if (result.saleRecommendations.length === 0) {
        return (
            <div className="container">
                <p>Keine Verkaufsempfehlung</p>
            </div>
        );
    }

    return (
        <div className="container">
            <Table striped bordered>
                <thead>
                <tr>
                    <th className="header">WKN</th>
                    <th className="header">Name</th>
                    <th className="header">Börse</th>
                    <th className="header">Preis</th>
                    <th className="header">Fiktiver Verkaufspreis</th>
                    <th className="header">Begründung</th>
                </tr>
                </thead>
                <tbody>
                {result.saleRecommendations &&
                 result.saleRecommendations.map(saleRecommendation => (
                     <SaleRecommendation
                         key={saleRecommendation.name}
                         saleRecommendation={saleRecommendation}
                         exchangeRsl={result.exchangeRsl}
                     />
                 ))}
                </tbody>
            </Table>
        </div>
    );
};

export default SalesRecommendations;
