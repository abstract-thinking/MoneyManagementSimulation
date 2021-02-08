import React from 'react';

const PurchaseRecommendation = ({purchaseRecommendation}) =>
                <tr>
                 <td>{purchaseRecommendation.wkn}</td>
                 <td>{purchaseRecommendation.name}</td>
                 <td>{purchaseRecommendation.exchange}</td>
                 <td style={{ textAlign: 'right' }}>{purchaseRecommendation.rsl.toFixed(2)}</td>
                 <td style={{ textAlign: 'right' }}>{purchaseRecommendation.exchangeRsl.toFixed(2)}</td>
                 <td style={{ textAlign: 'right' }}>{purchaseRecommendation.quantity}</td>
                 <td style={{ textAlign: 'right' }}>{purchaseRecommendation.price.toFixed(2)}</td>
                 <td style={{ textAlign: 'right' }}>{purchaseRecommendation.notionalSalesPrice.toFixed(2)}</td>
                </tr>

export default PurchaseRecommendation;
