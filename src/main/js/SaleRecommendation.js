import React, {useState} from "react";
import {Button, Modal} from "react-bootstrap";

const SaleRecommendation = ({
                                saleRecommendation,
                                exchangeRsl
                            }) => {
    const [show, setShow] = useState(false);

    const handleClose = () => setShow(false);
    const handleShow = () => setShow(true);

    return (
        <>
            <tr key={saleRecommendation.company}>
                <td className="text-content">{saleRecommendation.wkn}</td>
                <td className="text-content">{saleRecommendation.name}</td>
                <td className="number-content">{saleRecommendation.rsl}</td>
                <td className="number-content">{saleRecommendation.price}</td>
                <td className="number-content">
                    {saleRecommendation.notionalSalesPrice}
                </td>
                <td>
                    {saleRecommendation.shouldSellByFallingBelowTheLimit && (
                        <div>
                            {saleRecommendation.name} wöchentlicher Schlusskurs{" "}
                            {saleRecommendation.price} liegt unter dem kalkulierten
                            Verkaufspreis{" "}
                            {saleRecommendation.notionalSalesPrice}.
                        </div>
                    )}
                    {saleRecommendation.shouldSellByRslComparison && (
                        <div>
                            {saleRecommendation.name} RSL {saleRecommendation.rsl} liegt unter
                            dem RSL {exchangeRsl}.
                        </div>
                    )}
                </td>
                <td>
                    <Button variant="primary" onClick={handleShow}>-</Button>
                </td>
            </tr>
            <Modal show={show} onHide={handleClose}>
                <Modal.Header closeButton>
                    <Modal.Title>Modal heading</Modal.Title>
                </Modal.Header>
                <Modal.Body>Woohoo, you're reading this text in a modal!</Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={handleClose}>
                        Close
                    </Button>
                    <Button variant="primary" onClick={handleClose}>
                        Save Changes
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
};

export default SaleRecommendation;
